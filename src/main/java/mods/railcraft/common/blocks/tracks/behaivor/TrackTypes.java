/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.common.blocks.tracks.TrackConstants;

/**
 * Created by CovertJaguar on 8/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackTypes {
    ABANDONED,
    ELECTRIC,
    HIGH_SPEED,
    HIGH_SPEED_ELECTRIC,
    IRON,
    REINFORCED,
    STRAP_IRON,
    ;

    static {
        STRAP_IRON.speedController = SpeedControllerStrapIron.instance();

        REINFORCED.resistance = 80F;
        REINFORCED.speedController = SpeedControllerReinforced.instance();

        ELECTRIC.trackSpec = TrackSpecElectric.instance();
        HIGH_SPEED_ELECTRIC.trackSpec = TrackSpecElectric.instance();

        HIGH_SPEED.speedController = SpeedControllerHighSpeed.instance();
        HIGH_SPEED_ELECTRIC.speedController = SpeedControllerHighSpeed.instance();
    }

    private TrackSpec trackSpec = TrackSpec.instance();
    private SpeedController speedController = SpeedController.instance();
    private float resistance = TrackConstants.RESISTANCE;

    public float getResistance() {
        return resistance;
    }

    public TrackSpec getTrackSpec() {
        return trackSpec;
    }

    public SpeedController getSpeedController() {
        return speedController;
    }
}
