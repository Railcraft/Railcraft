/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackType;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TrackTileFactory {

    public static void initTrackTile(TileTrackOutfitted tile, TrackType trackType, TrackKit trackKit) {
        ITrackKitInstance trackInstance = trackKit.createInstance();
        tile.setTrackType(trackType);
        tile.setTrackKitInstance(trackInstance);
        trackInstance.setTile(tile);
    }

    private TrackTileFactory() {
    }

}
