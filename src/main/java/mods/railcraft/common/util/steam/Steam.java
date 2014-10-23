/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Steam
{

    public static final float COLD_TEMP = 20;
    public static final float BOILING_POINT = 100;
    public static final float SUPER_HEATED = 300;
    public static final float MAX_HEAT_LOW = 500F;
    public static final float MAX_HEAT_HIGH = 1000F;
    public static final float HEAT_STEP = 0.05f;
    public static final float FUEL_PER_BOILER_CYCLE = 8f;
    public static final float FUEL_HEAT_INEFFICIENCY = 0.8f;
    public static final float FUEL_PRESSURE_INEFFICIENCY = 4f;
    public static final int STEAM_PER_UNIT_WATER = 160;
    public static final int STEAM_PER_10RF = 5;
    public static final boolean BOILERS_EXPLODE = true;
}
