/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.entity.item.EntityMinecart;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;

public class TrackSpeedSwitch extends TrackSwitch {

    public TrackSpeedSwitch() {
        speedController = SpeedControllerHighSpeed.getInstance();
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        TrackSpeed.testCartSpeedForBasic(this, cart);
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED_SWITCH;
    }
}
