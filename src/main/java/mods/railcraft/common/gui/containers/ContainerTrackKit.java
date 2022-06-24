/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitRailcraft;

/**
 * Created by CovertJaguar on 6/23/2022 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerTrackKit<T extends TrackKitRailcraft> extends RailcraftContainer {
    public final T kit;

    public ContainerTrackKit(TileTrackOutfitted tile) {
        super(player -> TileRailcraft.isUsableByPlayerHelper(tile, player));
        //noinspection unchecked
        this.kit = (T) tile.getTrackKitInstance();
    }
}
