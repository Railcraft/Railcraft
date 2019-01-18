/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TrackKitUnsupported extends TrackKitRailcraft {

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block neighborBlock) {
    }

}
