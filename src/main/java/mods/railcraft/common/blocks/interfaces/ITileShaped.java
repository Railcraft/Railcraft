/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by CovertJaguar on 2/19/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileShaped {

    default AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return Block.FULL_BLOCK_AABB;
    }

    default AxisAlignedBB getCollisionBoundingBox(IBlockAccess world, BlockPos pos) {
        return getBoundingBox(world, pos);
    }

    default AxisAlignedBB getSelectedBoundingBox(IBlockAccess world, BlockPos pos) {
        return getBoundingBox(world, pos).offset(pos);
    }
}
