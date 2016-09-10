/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.fluids.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BoilerFuelTank extends StandardTank {

    public BoilerFuelTank(int capacity, TileEntity tile) {
        super(capacity, tile);
    }

    @Override
    public boolean matchesFilter(FluidStack fluidStack) {
        return !Fluids.WATER.is(fluidStack) && FuelManager.getBoilerFuelValue(fluidStack.getFluid()) > 0 && super.matchesFilter(fluidStack);
    }
}
