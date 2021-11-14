/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

/**
 * Tracks that can emit a redstone signal should implement
 * this interface.
 *
 * For example a detector track.
 *
 * A track cannot implement both ITrackPowered and ITrackEmitter.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITrackEmitter extends ITrackInstance
{

    /**
     * Return the redstone output of the track.
     *
     * @return true if powered
     */
    public int getPowerOutput();
}
