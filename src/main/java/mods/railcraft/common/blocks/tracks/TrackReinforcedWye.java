/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.entity.Entity;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerReinforced;

public class TrackReinforcedWye extends TrackWye {

    public TrackReinforcedWye() {
        speedController = SpeedControllerReinforced.getInstance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.REINFORCED_WYE;
    }

    @Override
    public float getExplosionResistance(double srcX, double srcY, double srcZ, Entity exploder) {
        return TrackReinforced.RESISTANCE;
    }
}
