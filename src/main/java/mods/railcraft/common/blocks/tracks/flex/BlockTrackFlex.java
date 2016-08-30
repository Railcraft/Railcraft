/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.BlockTrackStateless;
import net.minecraft.block.BlockRail;
import net.minecraft.block.properties.IProperty;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlex extends BlockTrackStateless {

    public BlockTrackFlex(TrackType trackType) {
        super(trackType);
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return BlockRail.SHAPE;
    }

}
