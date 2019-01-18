/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.steam;

import mods.railcraft.api.fuel.FluidFuelManager;
import mods.railcraft.common.fluids.FluidTools;
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
        return SteamConstants.HEAT_STEP;
    }

    @Override
    public double getMoreFuel() {
        FluidStack fuel = fuelTank.drain(FluidTools.BUCKET_VOLUME, false);
        if (fuel == null)
            return 0;

        double heatValue = FluidFuelManager.getFuelValueForSize(fuel);
        if (heatValue > 0) {
            fuelTank.drain(FluidTools.BUCKET_VOLUME, true);
        }
        return heatValue;
    }

}
