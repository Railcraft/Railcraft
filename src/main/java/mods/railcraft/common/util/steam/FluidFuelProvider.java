/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.api.fuel.FuelManager;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class FluidFuelProvider implements IFuelProvider {

    private final StandardTank fuelTank;

    public FluidFuelProvider(StandardTank fuelTank) {
        this.fuelTank = fuelTank;
    }

    @Override
    public double getHeatStep() {
        return Steam.HEAT_STEP;
    }

    @Override
    public double getMoreFuel() {
        FluidStack fuel = fuelTank.drain(FluidHelper.BUCKET_VOLUME, false);
        if (fuel == null)
            return 0;

        double heatValue = FuelManager.getBoilerFuelValue(fuel.getFluid());
        if (heatValue > 0) {
            fuelTank.drain(FluidHelper.BUCKET_VOLUME, true);
            if (fuel.amount < FluidHelper.BUCKET_VOLUME)
                heatValue *= (double) fuel.amount / (double) FluidHelper.BUCKET_VOLUME;
        }
        return heatValue;
    }

}
