/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.generic;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReplacerCube extends SimpleCube {

    public IBlockState replacementState;

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        replaceBlock(world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos) {
        replaceBlock(world, pos);
    }

    private void replaceBlock(World world, BlockPos pos) {
        if (replacementState != null)
            WorldPlugin.setBlockState(world, pos, replacementState);
    }
}
