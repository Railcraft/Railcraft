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

public class TrackReinforcedSwitch extends TrackSwitch {

    public TrackReinforcedSwitch() {
        speedController = SpeedControllerReinforced.getInstance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.REINFORCED_SWITCH;
    }

    @Override
    public float getExplosionResistance(double srcX, double srcY, double srcZ, Entity exploder) {
        return TrackReinforced.RESISTANCE;
    }
}
