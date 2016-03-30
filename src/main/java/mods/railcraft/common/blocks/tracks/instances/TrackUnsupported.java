/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TrackUnsupported extends TrackBaseRailcraft {

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock) {
    }

}
