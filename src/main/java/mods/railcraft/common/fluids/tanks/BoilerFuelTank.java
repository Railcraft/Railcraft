/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.fluids.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BoilerFuelTank extends StandardTank {

    public BoilerFuelTank(int capacity, TileEntity tile) {
        super(capacity, tile);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null)
            return 0;
        if (Fluids.WATER.get() == resource.getFluid())
            return 0;
        if (FuelManager.getBoilerFuelValue(resource.getFluid()) > 0)
            return super.fill(resource, doFill);
        return 0;
    }

}
