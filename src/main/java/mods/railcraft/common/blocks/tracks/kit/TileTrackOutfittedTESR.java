/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kit;

public class TileTrackOutfittedTESR extends TileTrackOutfitted {
    @Override
    public double getMaxRenderDistanceSquared() {
        return Short.MAX_VALUE;
    }
}
