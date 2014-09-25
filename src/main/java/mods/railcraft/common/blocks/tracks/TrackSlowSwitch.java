/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerSlow;

public class TrackSlowSwitch extends TrackSwitch
{

    public TrackSlowSwitch()
    {
        speedController = SpeedControllerSlow.getInstance();
    }

    @Override
    public EnumTrack getTrackType()
    {
        return EnumTrack.SLOW_SWITCH;
    }
}
