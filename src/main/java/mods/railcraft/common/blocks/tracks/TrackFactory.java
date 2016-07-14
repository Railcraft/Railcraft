/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackFactory {

    public static TileTrack makeTrackTile(int trackID) {
        TrackSpec spec = TrackRegistry.getTrackSpec(trackID);
        return makeTrackTile(spec);
    }

    public static TileTrack makeTrackTile(TrackSpec trackSpec) {
        ITrackInstance trackInstance = trackSpec.createInstanceFromSpec();
        TileTrack tileTrack;
        if (trackSpec == EnumTrack.BUFFER_STOP.getTrackSpec())
            tileTrack = new TileTrackTESR();
        else if (trackInstance.canUpdate())
            tileTrack = new TileTrackTicking();
        else
            tileTrack = new TileTrack();
        tileTrack.track = trackInstance;
        trackInstance.setTile(tileTrack);
        return tileTrack;
    }

}
