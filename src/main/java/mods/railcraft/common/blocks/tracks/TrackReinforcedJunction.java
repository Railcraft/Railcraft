/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerReinforced;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;

public class TrackReinforcedJunction extends TrackJunction {

    public TrackReinforcedJunction() {
        speedController = SpeedControllerReinforced.getInstance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.REINFORCED_JUNCTION;
    }
    
    @Override
    public float getExplosionResistance(Explosion explosion, Entity exploder) {
        return TrackReinforced.RESISTANCE;
    }
}
