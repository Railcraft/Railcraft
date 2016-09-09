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
import mods.railcraft.common.util.misc.SafeNBTWrapper;
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

import javax.annotation.Nullable;
import java.io.IOException;

public class TileFluidLoader extends TileFluidManipulator {

    private static final int RESET_WAIT = 200;
    private static final int TRANSFER_RATE = 20;
    private static final float MAX_PIPE_LENGTH = 16 * 0.0625f;
    private static final float PIPE_INCREMENT = 0.01f;
    private float pipeLength;
    private boolean needsPipe;

    public TileFluidLoader() {
        tankManager.add(loaderTank);
    }

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.FLUID_LOADER;
    }

    public IInventory getInputInventory() {
        return invInput;
    }

    @Override
    public EnumFacing getFacing() {
        return EnumFacing.DOWN;
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
    protected void reset() {
        if (currentCart instanceof IFluidCart)
            ((IFluidCart) currentCart).setFilling(false);
        setResetTimer(0);
    }

    @Override
    protected void upkeep() {
        super.upkeep();

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
    }

    @Nullable
    @Override
    public EntityMinecart getCart() {
        needsPipe = false;
        EntityMinecart cart = super.getCart();
        if (cart == null) {
            cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos().down(), 0.2f, EnumFacing.DOWN);
            needsPipe = true;
        }
        return cart;
    }

    @Override
    protected void waitForReset(@Nullable EntityMinecart cart) {
        if (pipeIsRetracted())
            sendCart(cart);
        else
            retractPipe();
    }

    @Override
    protected void onNoCart() {
        if (!pipeIsRetracted())
            retractPipe();
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        if (cart instanceof EntityLocomotiveSteam) {
            EntityLocomotiveSteam loco = (EntityLocomotiveSteam) cart;
            if (!loco.isSafeToFill()) {
                retractPipe();
                return;
            }
        }

        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        boolean cartNeedsFilling = cartNeedsFilling(tankCart);

        if (cartNeedsFilling && needsPipe)
            extendPipe();
        else
            retractPipe();

        setProcessing(false);
        if (cartNeedsFilling && (!needsPipe || pipeIsExtended())) {
            FluidStack drained = tankManager.drain(0, RailcraftConfig.getTankCartFillRate(), false);
            if (drained != null) {
                int flow = tankCart.fill(EnumFacing.UP, drained, true);
                tankManager.drain(0, flow, true);
                setProcessing(flow > 0);
            }
        }

        if (isProcessing())
            setPowered(false);

        if (cart instanceof IFluidCart)
            ((IFluidCart) cart).setFilling(isProcessing());

        if (tankCart.isTankFull(loaderTank.getFluidType()))
            setResetTimer(RESET_WAIT);
    }

    private boolean cartNeedsFilling(TankToolkit tankCart) {
        FluidStack loaderLiquid = loaderTank.getFluid();
        return loaderLiquid != null && loaderLiquid.amount > 0 && tankCart.canPutFluid(EnumFacing.UP, loaderLiquid);
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        if (!pipeIsRetracted() || isProcessing())
            return true;
        if (!(cart instanceof IFluidHandler))
            return false;
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        Fluid fluidHandled = getFluidHandled();
        if (!loaderTank.isEmpty() && !tankCart.canPutFluid(EnumFacing.UP, loaderTank.getFluid()))
            return false;
        else if (getRedstoneModeController().getButtonState() != EnumRedstoneMode.COMPLETE && !tankCart.isTankEmpty(fluidHandled))
            return false;
        else if (getRedstoneModeController().getButtonState() == EnumRedstoneMode.IMMEDIATE && tankCart.isTankEmpty(fluidHandled))
            return false;
        return !tankCart.isTankFull(fluidHandled);
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

        data.setFloat("pipeLength", pipeLength);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        SafeNBTWrapper safe = new SafeNBTWrapper(data);
        pipeLength = safe.getFloat("pipeLength");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeFloat(pipeLength);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
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
}
