/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.ITrackKitLockdown;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.carts.EntityLocomotiveSteam;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankToolkit;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.SafeNBTWrapper;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.IOException;

public class TileFluidLoader extends TileFluidManipulator implements IGuiReturnHandler {

    private static final int RESET_WAIT = 200;
    private static final int TRANSFER_RATE = 20;
    private static final float MAX_PIPE_LENGTH = 16 * 0.0625f;
    private static final float PIPE_INCREMENT = 0.01f;
    private final MultiButtonController<ButtonState> stateController = MultiButtonController.create(ButtonState.FORCE_FULL.ordinal(), ButtonState.values());
    private int waitForReset;
    private float pipeLength;

    public TileFluidLoader() {
        tankManager.add(loaderTank);
    }

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.FLUID_LOADER;
    }

    public MultiButtonController<ButtonState> getStateController() {
        return stateController;
    }

    public IInventory getInputInventory() {
        return invInput;
    }

    private void resetPipe() {
        pipeLength = 0;
    }

    public float getPipeLength() {
        return pipeLength;
    }

    private void setPipeLength(float y) {
        pipeLength = y;
        sendUpdateToClient();
    }

    private void extendPipe() {
        float y = pipeLength + PIPE_INCREMENT;
        if (pipeIsExtended())
            y = MAX_PIPE_LENGTH;
        setPipeLength(y);
    }

    private void retractPipe() {
        float y = pipeLength - PIPE_INCREMENT;
        if (pipeIsRetracted())
            y = 0;
        setPipeLength(y);
    }

    private boolean pipeIsExtended() {
        return pipeLength >= MAX_PIPE_LENGTH;
    }

    private boolean pipeIsRetracted() {
        return pipeLength <= 0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getX(), getY() - 1, getZ(), getX() + 1, getY() + 1, getZ() + 1);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(getWorld()))
            return;

        ItemStack topSlot = getStackInSlot(SLOT_INPUT);
        if (topSlot != null && !FluidItemHelper.isContainer(topSlot)) {
            setInventorySlotContents(SLOT_INPUT, null);
            dropItem(topSlot);
        }

        ItemStack bottomSlot = getStackInSlot(SLOT_OUTPUT);
        if (bottomSlot != null && !FluidItemHelper.isContainer(bottomSlot)) {
            setInventorySlotContents(SLOT_OUTPUT, null);
            dropItem(bottomSlot);
        }

        if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
            FluidHelper.drainContainers(this, this, SLOT_INPUT, SLOT_OUTPUT);

        for (EnumFacing side : EnumFacing.values()) {
            if (side == null)
                continue;
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof IFluidHandler) {
                IFluidHandler nearbyTank = (IFluidHandler) tile;
                side = side.getOpposite();
                Fluid filterFluid = getFilterFluid();
                if (filterFluid != null) {
                    FluidStack drained = nearbyTank.drain(side, new FluidStack(filterFluid, TRANSFER_RATE), false);
                    int used = loaderTank.fill(drained, true);
                    nearbyTank.drain(side, new FluidStack(filterFluid, used), true);
                } else {
                    FluidStack drained = nearbyTank.drain(side, TRANSFER_RATE, false);
                    int used = loaderTank.fill(drained, true);
                    nearbyTank.drain(side, used, true);
                }
            }
        }

        boolean needsPipe = false;

        EntityMinecart cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos(), 0.2f, EnumFacing.DOWN);
        if (cart == null) {
            cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos().down(), 0.2f, EnumFacing.DOWN);
            needsPipe = true;
        }

        if (cart != currentCart) {
            if (currentCart instanceof IFluidCart)
                ((IFluidCart) currentCart).setFilling(false);
            setPowered(false);
            currentCart = cart;
            cartWasSent();
            waitForReset = 0;
        }

        if (waitForReset > 0)
            waitForReset--;

        if (waitForReset > 0) {
            if (pipeIsRetracted())
                sendCart(cart);
            else
                retractPipe();
            return;
        }

        if (cart == null) {
            if (!pipeIsRetracted())
                retractPipe();
            return;
        }

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        if (cart instanceof EntityLocomotiveSteam) {
            EntityLocomotiveSteam loco = (EntityLocomotiveSteam) cart;
            if (!loco.isSafeToFill()) {
                retractPipe();
                return;
            }
        }

        if (isPaused())
            return;

        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        boolean cartNeedsFilling = cartNeedsFilling(tankCart);

        if (cartNeedsFilling && needsPipe)
            extendPipe();
        else
            retractPipe();

        flow = 0;
        if (cartNeedsFilling && (!needsPipe || pipeIsExtended())) {
            FluidStack drained = tankManager.drain(0, RailcraftConfig.getTankCartFillRate(), false);
            if (drained != null) {
                flow = tankCart.fill(EnumFacing.UP, drained, true);
                tankManager.drain(0, flow, true);
            }
        }

        boolean flowed = flow > 0;
        if (flowed)
            setPowered(false);

        if (cart instanceof IFluidCart)
            ((IFluidCart) cart).setFilling(flowed);

        if (tankCart.isTankFull(loaderTank.getFluidType()))
            waitForReset = RESET_WAIT;

        if (stateController.getButtonState() != ButtonState.MANUAL
                && pipeIsRetracted() && flow <= 0 && shouldSendCart(cart))
            sendCart(cart);
    }

    private boolean cartNeedsFilling(TankToolkit tankCart) {
        FluidStack loaderLiquid = loaderTank.getFluid();
        return loaderLiquid != null && loaderLiquid.amount > 0 && tankCart.canPutFluid(EnumFacing.UP, loaderLiquid);
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof IFluidHandler))
            return true;
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        Fluid fluidHandled = getFluidHandled();
        if (!loaderTank.isEmpty() && !tankCart.canPutFluid(EnumFacing.UP, loaderTank.getFluid()))
            return true;
        else if (stateController.getButtonState() != ButtonState.FORCE_FULL && !tankCart.isTankEmpty(fluidHandled))
            return true;
        else if (stateController.getButtonState() == ButtonState.IMMEDIATE && tankCart.isTankEmpty(fluidHandled))
            return true;
        else if (tankCart.isTankFull(fluidHandled))
            return true;
        return false;
    }

    @Override
    protected void setPowered(boolean p) {
        if (isManualMode())
            p = false;
        if (p) {
            resetPipe();
            if (worldObj != null) {
                TileEntity tile = worldObj.getTileEntity(getPos().down(2));
                if (tile instanceof TileTrackOutfitted) {
                    TileTrackOutfitted trackTile = (TileTrackOutfitted) tile;
                    ITrackKitInstance track = trackTile.getTrackKitInstance();
                    if (track instanceof ITrackKitLockdown)
                        ((ITrackKitLockdown) track).releaseCart();
                }
            }
        }
        super.setPowered(p);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        resetPipe();
    }

    @Override
    public void validate() {
        super.validate();
        resetPipe();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        resetPipe();
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        Fluid fluidFilter = getFilterFluid();
        if (resource == null || (fluidFilter != null && !Fluids.areEqual(fluidFilter, resource)))
            return 0;
        return super.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        Fluid fluidFilter = getFilterFluid();
        return fluidFilter == null || fluid.equals(fluidFilter);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        stateController.writeToNBT(data, "state");

        data.setFloat("pipeLength", pipeLength);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        stateController.readFromNBT(data, "state");

        SafeNBTWrapper safe = new SafeNBTWrapper(data);
        pipeLength = safe.getFloat("pipeLength");

        // Legacy code
        boolean waitIfEmpty = data.getBoolean("WaitIfEmpty");
        boolean waitTillFull = data.getBoolean("WaitTillFull");
        if (waitTillFull)
            stateController.setCurrentState(ButtonState.FORCE_FULL);
        else if (waitIfEmpty)
            stateController.setCurrentState(ButtonState.HOLD_EMPTY);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(stateController.getCurrentState());
        data.writeFloat(pipeLength);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        stateController.setCurrentState(data.readByte());
        setPipeLength(data.readFloat());
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_FLUID, player, worldObj, getPos());
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_INPUT:
                return FluidItemHelper.isFilledContainer(stack);
        }
        return false;
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte(stateController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        stateController.setCurrentState(data.readByte());
    }

    @Override
    public boolean isManualMode() {
        return stateController.getButtonState() == ButtonState.MANUAL;
    }

    public enum ButtonState implements IMultiButtonState {

        HOLD_EMPTY("railcraft.gui.liquid.loader.empty"),
        FORCE_FULL("railcraft.gui.liquid.loader.fill"),
        IMMEDIATE("railcraft.gui.liquid.loader.immediate"),
        MANUAL("railcraft.gui.liquid.loader.manual");
        private final String label;
        private final ToolTip tip;

        ButtonState(String label) {
            this.label = label;
            this.tip = ToolTip.buildToolTip(label + ".tip");
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate(label);
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tip;
        }
    }
}
