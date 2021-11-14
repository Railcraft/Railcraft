/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

import net.minecraft.util.AxisAlignedBB;

public interface ITrackSwitch extends ITrackInstance {
    /**
     * Returns whether the switch track should "appear" switched.
     * Has no influence on where a cart actually ends up.
     * This is mostly due to issues with the visual state updating slower than carts can approach the track.
     *
     * @return true if the track appears switched
     */
    public boolean isVisuallySwitched();

    /**
     * Switch Tracks have an additional orientation state beyond normal tracks.
     * This function returns that value.
     *
     * @return true if the track is mirrored
     */
    public boolean isMirrored();
}
