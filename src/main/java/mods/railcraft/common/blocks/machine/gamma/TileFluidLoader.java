/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.carts.ILiquidTransfer;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackLockdown;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.carts.EntityCartTank;
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
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileFluidLoader extends TileLoaderFluidBase implements IGuiReturnHandler {
    private static final int RESET_WAIT = 200;
    private static final int TRANSFER_RATE = 20;
    private static final float MAX_PIPE_LENGTH = 16 * 0.0625f;
    private static final float PIPE_INCREMENT = 0.01f;
    private final MultiButtonController<ButtonState> stateController = new MultiButtonController<ButtonState>(ButtonState.FORCE_FULL.ordinal(), ButtonState.values());
    private int waitForReset = 0;
    private float pipeLenght = 0;

    public TileFluidLoader() {
        super();
        tankManager.add(loaderTank);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.FLUID_LOADER;
    }

    public MultiButtonController<ButtonState> getStateController() {
        return stateController;
    }

    public IInventory getInputInventory() {
        return invInput;
    }

    private void resetPipe() {
        pipeLenght = 0;
    }

    public float getPipeLenght() {
        return pipeLenght;
    }

    private void setPipeLength(float y) {
        pipeLenght = y;
        sendUpdateToClient();
    }

    private void extendPipe() {
        float y = pipeLenght + PIPE_INCREMENT;
        if (pipeIsExtended())
            y = MAX_PIPE_LENGTH;
        setPipeLength(y);
    }

    private void retractPipe() {
        float y = pipeLenght - PIPE_INCREMENT;
        if (pipeIsRetracted())
            y = 0;
        setPipeLength(y);
    }

    private boolean pipeIsExtended() {
        return pipeLenght >= MAX_PIPE_LENGTH;
    }

    private boolean pipeIsRetracted() {
        return pipeLenght <= 0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public IIcon getIcon(int side) {
        if (side > 1)
            return getMachineType().getTexture(6);
        return getMachineType().getTexture(side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld()))
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

        for (ForgeDirection side : ForgeDirection.values()) {
            if (side == ForgeDirection.UNKNOWN)
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

        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0.2f, ForgeDirection.DOWN);
        if (cart == null) {
            cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord - 1, zCoord, 0.2f, ForgeDirection.DOWN);
            needsPipe = true;
        }

        if (cart != currentCart) {
            if (currentCart instanceof IFluidCart)
                ((IFluidCart) currentCart).setFilling(false);
            else if (currentCart instanceof ILiquidTransfer)
                ((ILiquidTransfer) currentCart).setFilling(false);
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
                flow = tankCart.fill(ForgeDirection.UP, drained, true);
                tankManager.drain(0, flow, true);
            }
        }

        boolean flowed = flow > 0;
        if (flowed)
            setPowered(false);

        if (cart instanceof IFluidCart)
            ((IFluidCart) cart).setFilling(flowed);
        else if (cart instanceof ILiquidTransfer)
            ((ILiquidTransfer) cart).setFilling(flowed);

        if (tankCart.isTankFull(loaderTank.getFluidType()))
            waitForReset = RESET_WAIT;

        if (stateController.getButtonState() != ButtonState.MANUAL
                && pipeIsRetracted() && flow <= 0 && shouldSendCart(cart))
            sendCart(cart);
    }

    private boolean cartNeedsFilling(TankToolkit tankCart) {
        FluidStack loaderLiquid = loaderTank.getFluid();
        return loaderLiquid != null && loaderLiquid.amount > 0 && tankCart.canPutFluid(ForgeDirection.UP, loaderLiquid);
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof IFluidHandler))
            return true;
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        Fluid fluidHandled = getFluidHandled();
        if (!loaderTank.isEmpty() && !tankCart.canPutFluid(ForgeDirection.UP, loaderTank.getFluid()))
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
                TileEntity tile = worldObj.getTileEntity(xCoord, yCoord - 2, zCoord);
                if (tile instanceof TileTrack) {
                    TileTrack trackTile = (TileTrack) tile;
                    ITrackInstance track = trackTile.getTrackInstance();
                    if (track instanceof ITrackLockdown)
                        ((ITrackLockdown) track).releaseCart();
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
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        Fluid fluidFilter = getFilterFluid();
        if (resource == null || (fluidFilter != null && !Fluids.areEqual(fluidFilter, resource)))
            return 0;
        return super.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        Fluid fluidFilter = getFilterFluid();
        return fluidFilter == null || fluid.equals(fluidFilter);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        stateController.writeToNBT(data, "state");

        data.setFloat("pipeLenght", pipeLenght);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        stateController.readFromNBT(data, "state");

        SafeNBTWrapper safe = new SafeNBTWrapper(data);
        pipeLenght = safe.getFloat("pipeLenght");

        // Legacy code
        boolean waitIfEmpty = data.getBoolean("WaitIfEmpty");
        boolean waitTillFull = data.getBoolean("WaitTillFull");
        if (waitTillFull)
            stateController.setCurrentState(ButtonState.FORCE_FULL);
        else if (waitIfEmpty)
            stateController.setCurrentState(ButtonState.HOLD_EMPTY);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(stateController.getCurrentState());
        data.writeFloat(pipeLenght);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        stateController.setCurrentState(data.readByte());
        setPipeLength(data.readFloat());
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_FLUID, player, worldObj, xCoord, yCoord, zCoord);
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
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(stateController.getCurrentState());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
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

        private ButtonState(String label) {
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
