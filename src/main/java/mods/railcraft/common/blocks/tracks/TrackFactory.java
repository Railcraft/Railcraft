/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import org.apache.logging.log4j.Level;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackFactory {

    public static TileTrack makeTrackTile(int trackID) {
        TrackSpec spec = TrackRegistry.getTrackSpec(trackID);
        if (spec == null) {
            Game.log(Level.ERROR, "Attempted to create Track Tile with invalid Track ID {0}", trackID);
            return null;
        }
        return makeTrackTile(spec.createInstanceFromSpec());
    }

    public static TileTrack makeTrackTile(ITrackInstance track) {
        if (track instanceof TrackBufferStop) {
            return new TileTrackTESR(track);
        }
        return new TileTrack(track);
    }

}
