/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.util.misc.Game;

public class TrackBoardingTrain extends TrackBoarding {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.BOARDING_TRAIN;
    }

    @Override
    public void updateEntity() {
        if (Game.isHost(getWorld())) {
            TrackNextGenLocking.LockingProfileType type;
            if (isReversed())
                type = TrackNextGenLocking.LockingProfileType.BOARDING_B_TRAIN;
            else
                type = TrackNextGenLocking.LockingProfileType.BOARDING_A_TRAIN;
            migrateTrack(type);
        }
    }

}
