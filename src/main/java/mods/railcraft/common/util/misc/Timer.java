/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Timer {

    private long startTime = Long.MIN_VALUE;

    public boolean hasTriggered(World world, int ticks) {
        long currentTime = world.getTotalWorldTime();
        if (currentTime >= (ticks + startTime) || startTime > currentTime) {
            startTime = currentTime;
            return true;
        }
        return false;
    }

    public void reset() {
        startTime = Long.MIN_VALUE;
    }

}
