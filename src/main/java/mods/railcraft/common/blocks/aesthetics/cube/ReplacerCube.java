/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ReplacerCube extends SimpleCube {

    public Block block = null;
    public int meta = 0;

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        replaceBlock(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        replaceBlock(world, x, y, z);
    }

    private void replaceBlock(World world, int x, int y, int z) {
        if (block != null) {
            world.setBlock(x, y, z, block, meta, 3);
        }
    }

}
