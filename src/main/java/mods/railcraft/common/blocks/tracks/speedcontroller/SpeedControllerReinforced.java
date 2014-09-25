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
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedControllerReinforced extends SpeedController
{

    public static final float MAX_SPEED = 0.499f;
    public static final float CORNER_SPEED = 0.4f;
    private static SpeedControllerReinforced instance;

    public static SpeedControllerReinforced getInstance()
    {
        if(instance == null) {
            instance = new SpeedControllerReinforced();
        }
        return instance;
    }

    @Override
    public float getMaxSpeed(ITrackInstance track, EntityMinecart cart)
    {
        switch (EnumTrackMeta.fromMeta(track.getBasicRailMetadata(cart))) {
            case EAST_NORTH_CORNER:
            case EAST_SOUTH_CORNER:
            case WEST_NORTH_CORNER:
            case WEST_SOUTH_CORNER:
                return CORNER_SPEED;
            default:
                return MAX_SPEED;
        }
    }
}
