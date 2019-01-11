/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.AdvancedFluidHandler;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class TileFluidUnloader extends TileFluidManipulator {

    private static final int TRANSFER_RATE = 80;

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.FLUID_UNLOADER;
    }

    @Override
    public EnumFacing getDefaultFacing() {
        return EnumFacing.UP;
    }

    @Override
    protected void upkeep() {
        super.upkeep();

        if (clock % FluidTools.BUCKET_FILL_TIME == 0)
            FluidTools.fillContainers(tankManager, this, SLOT_INPUT, SLOT_OUTPUT, tank.getFluidType());

        tankManager.push(tileCache, Predicates.notInstanceOf(getClass()), EnumFacing.VALUES, 0, TRANSFER_RATE);
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        AdvancedFluidHandler tankCart = getFluidHandler(cart, EnumFacing.DOWN);
        if (tankCart != null) {
            FluidStack moved = FluidUtil.tryFluidTransfer(tank, tankCart, RailcraftConfig.getTankCartFillRate(), true);
            setProcessing(Fluids.nonEmpty(moved));
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        AdvancedFluidHandler tankCart = getFluidHandler(cart, EnumFacing.DOWN);
        if (tankCart == null)
            return false;
        if (redstoneController().is(EnumRedstoneMode.IMMEDIATE))
            return false;
        if (getFilterFluid() != null && tankCart.isTankEmpty(getFilterFluid()))
            return false;
        return !tankCart.areTanksEmpty();
    }
}
