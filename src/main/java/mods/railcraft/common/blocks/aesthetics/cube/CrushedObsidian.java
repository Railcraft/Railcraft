/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import java.util.Random;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CrushedObsidian extends SimpleCube {

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        world.scheduleBlockUpdate(i, j, k, BlockCube.getBlock(), this.tickRate());
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
        world.scheduleBlockUpdate(i, j, k, BlockCube.getBlock(), this.tickRate());
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random rand) {
        this.tryToFall(world, i, j, k);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate() {
        return 3;
    }

    /**
     * If there is space to fall below will start this block falling
     */
    private void tryToFall(World par1World, int x, int y, int z) {
        if (canFallBelow(par1World, x, y - 1, z) && y >= 0) {
            byte var8 = 32;

            if (!BlockSand.fallInstantly && par1World.checkChunksExist(x - var8, y - var8, z - var8, x + var8, y + var8, z + var8)) {
                if (!par1World.isRemote) {
                    EntityFallingBlock entity = new EntityFallingBlock(par1World, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BlockCube.getBlock(), EnumCube.CRUSHED_OBSIDIAN.ordinal());
                    par1World.spawnEntityInWorld(entity);
                }
            } else {
                par1World.setBlockToAir(x, y, z);

                while (canFallBelow(par1World, x, y - 1, z) && y > 0) {
                    --y;
                }

                if (y > 0)
                    par1World.setBlock(x, y, z, BlockCube.getBlock(), EnumCube.CRUSHED_OBSIDIAN.ordinal(), 3);
            }
        }
    }

    /**
     * Checks to see if the sand can fall into the block below it
     */
    public static boolean canFallBelow(World world, int x, int y, int z) {
        if (world.isAirBlock(x, y, z))
            return true;

        Block block = WorldPlugin.getBlock(world, x, y, z);
        if (block == Blocks.fire)
            return true;

        return block.getMaterial().isLiquid();
    }

}
