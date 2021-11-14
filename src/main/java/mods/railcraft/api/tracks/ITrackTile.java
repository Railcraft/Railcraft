/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

/**
 * Don't use this, its an interface that allows other API code
 * access to internal functions of the code.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITrackTile
{

    public ITrackInstance getTrackInstance();
    
    public void sendUpdateToClient();
}
