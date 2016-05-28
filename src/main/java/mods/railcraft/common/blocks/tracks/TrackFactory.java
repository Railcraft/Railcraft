/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mcp.MethodsReturnNonnullByDefault;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TrackFactory {

    public static TileTrack makeTrackTile(int trackID) {
        TrackSpec spec = TrackRegistry.getTrackSpec(trackID);
        return makeTrackTile(spec);
    }

    public static TileTrack makeTrackTile(TrackSpec trackSpec) {
        TileTrack tileTrack;
        if (trackSpec == EnumTrack.BUFFER_STOP.getTrackSpec())
            tileTrack = new TileTrackTESR();
        else
            tileTrack = new TileTrack();
        tileTrack.makeTrackInstance(trackSpec);
        return tileTrack;
    }

}
