/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.common.plugins.thaumcraft.EssentiaTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EssentiaFuelProvider implements IFuelProvider {

    private static final int ESSENTIA_HEAT_VALUE = 400;
    private final EssentiaTank tank;

    public EssentiaFuelProvider(EssentiaTank tank) {
        this.tank = tank;
    }

    @Override
    public double getHeatStep() {
        return Steam.HEAT_STEP;
    }

    @Override
    public double getMoreFuel() {
        if (tank.contains(1)) {
            tank.remove(1, true);
            return ESSENTIA_HEAT_VALUE;
        }
        return 0;
    }

}
