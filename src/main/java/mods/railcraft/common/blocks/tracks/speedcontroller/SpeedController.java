/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.speedcontroller;

import net.minecraft.entity.item.EntityMinecart;
import mods.railcraft.api.tracks.ITrackInstance;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedController
{

    private static SpeedController instance;

    public static SpeedController getInstance()
    {
        if(instance == null) {
            instance = new SpeedController();
        }
        return instance;
    }

    public float getMaxSpeed(ITrackInstance track, EntityMinecart cart)
    {
        return 0.4f;
    }
}
