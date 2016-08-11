/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.TrackKit;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTileFactory {

    public static TileTrackOutfitted makeTrackTile(TrackKit trackKit) {
        ITrackKitInstance trackInstance = trackKit.createInstanceFromSpec();
        TileTrackOutfitted tileTrack;
        if (trackKit == TrackKits.BUFFER_STOP.getTrackKit())
            tileTrack = new TileTrackOutfittedTESR();
        else if (trackInstance.canUpdate())
            tileTrack = new TileTrackOutfittedTicking();
        else
            tileTrack = new TileTrackOutfitted();
        tileTrack.track = trackInstance;
        trackInstance.setTile(tileTrack);
        return tileTrack;
    }

}
