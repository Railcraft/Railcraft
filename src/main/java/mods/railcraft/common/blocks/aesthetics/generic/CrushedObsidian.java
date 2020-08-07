/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.generic;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CrushedObsidian extends SimpleCube {

    /**
     * Checks to see if the sand can fall into the block below it
     */
    public static boolean canFallInto(World world, BlockPos pos) {
        if (WorldPlugin.isBlockAir(world, pos))
            return true;

        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return state.getBlock() == Blocks.FIRE || state.getMaterial().isLiquid();

    }

    @Override
    public boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos) {
        world.scheduleBlockUpdate(pos, BlockGeneric.getBlock(), tickRate(), 0);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        world.scheduleBlockUpdate(pos, BlockGeneric.getBlock(), tickRate(), 0);
    }

    @Override
    public void updateTick(World world, BlockPos pos, Random rand) {
        tryToFall(world, pos);
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
    private void tryToFall(World world, BlockPos pos) {
        if (canFallInto(world, pos.down()) && pos.getY() >= 0) {
            byte size = 32;

            if (!BlockSand.fallInstantly && WorldPlugin.isAreaLoaded(world, pos.add(-size, -size, -size), pos.add(size, size, size))) {
                if (!world.isRemote) {
                    EntityFallingBlock entity = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.getBlockState(pos));
                    world.spawnEntity(entity);
                }
            } else {
                WorldPlugin.setBlockToAir(world, pos);
                BlockPos blockPos;

                for (blockPos = pos.down(); canFallInto(world, blockPos) && blockPos.getY() > 0; blockPos = blockPos.down()) {
                    // NOOP
                }

                if (blockPos.getY() > 0)
                    WorldPlugin.setBlockState(world, blockPos.up(), BlockGeneric.getBlock().getDefaultState().withProperty(BlockGeneric.getBlock().getVariantEnumProperty(), EnumGeneric.CRUSHED_OBSIDIAN));
            }
        }
    }

}
