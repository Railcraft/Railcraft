/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedController;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerReinforced;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerStrapIron;

/**
 * Created by CovertJaguar on 8/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackTypes {
    IRON(SpeedController.instance()),
    STRAP_IRON(SpeedControllerStrapIron.instance()),
    REINFORCED(SpeedControllerReinforced.instance()) {
        @Override
        public float getResistance() {
            return 80F;
        }
    },
    HIGH_SPEED(SpeedControllerHighSpeed.instance()),
    HIGH_SPEED_ELECTRIC(SpeedControllerHighSpeed.instance()),
    ELECTRIC(SpeedController.instance());

    public final SpeedController speedController;

    TrackTypes(SpeedController speedController) {
        this.speedController = speedController;
    }

    public float getResistance() {
        return TrackConstants.RESISTANCE;
    }
}
