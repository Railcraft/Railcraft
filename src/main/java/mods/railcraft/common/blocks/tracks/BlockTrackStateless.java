/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.TrackType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by CovertJaguar on 8/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTrackStateless extends BlockTrack {
    private final TrackType trackType;

    protected BlockTrackStateless(TrackType trackType) {
        this.trackType = trackType;
        setResistance(trackType.getResistance());
    }

    public TrackType getTrackType() {
        return trackType;
    }

    @Override
    public TrackType getTrackType(IBlockAccess world, BlockPos pos) {
        return trackType;
    }
}
