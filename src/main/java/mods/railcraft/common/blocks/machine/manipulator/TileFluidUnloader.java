/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFluidUnloader extends TileFluidManipulator {

    private static final int TRANSFER_RATE = 80;

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.FLUID_UNLOADER;
    }

    @Override
    public EnumFacing getFacing() {
        return EnumFacing.UP;
    }

    @Override
    protected void upkeep() {
        super.upkeep();

        if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
            FluidHelper.fillContainers(tankManager, this, SLOT_INPUT, SLOT_OUTPUT, loaderTank.getFluidType());

        tankManager.outputLiquid(tileCache, TankManager.TANK_FILTER, EnumFacing.VALUES, 0, TRANSFER_RATE);
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);

        FluidStack drained = tankCart.drain(EnumFacing.DOWN, RailcraftConfig.getTankCartFillRate(), false);
        if (getFilterFluid() == null || Fluids.areEqual(getFilterFluid(), drained)) {
            int flow = tankManager.get(0).fill(drained, true);
            tankCart.drain(EnumFacing.DOWN, flow, true);
            setProcessing(flow > 0);
        }
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        if (isProcessing())
            return true;
        if (!(cart instanceof IFluidHandler))
            return false;
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        if (getRedstoneModeController().getButtonState() == EnumRedstoneMode.IMMEDIATE)
            return false;
        if (getFilterFluid() != null && tankCart.isTankEmpty(getFilterFluid()))
            return false;
        return !tankCart.areTanksEmpty();
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.UNLOADER_FLUID, player, worldObj, getPos());
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_INPUT:
                return FluidItemHelper.isEmptyContainer(stack);
        }
        return false;
    }
}
