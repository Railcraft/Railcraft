/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
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

import java.util.stream.Stream;

public class TileFluidUnloader extends TileFluidManipulator {

    private static final int TRANSFER_RATE = 250;
    private static final int TRANSFER_TIME = 3;
    private static final EnumFacing[] PUSH_TO = Stream.of(EnumFacing.VALUES).filter(f -> f != EnumFacing.UP).toArray(EnumFacing[]::new);

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

        if (clock % TRANSFER_TIME == 0)
            tankManager.push(tileCache, Predicates.notInstanceOf(getClass()), 0, TRANSFER_RATE, PUSH_TO);
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
        if (getFilterFluid().map(tankCart::isTankEmpty).orElse(false))
            return false;
        return !tankCart.areTanksEmpty();
    }
}
