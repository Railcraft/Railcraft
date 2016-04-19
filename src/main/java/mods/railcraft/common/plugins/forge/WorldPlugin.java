/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class WorldPlugin {

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    public static Block getBlock(IBlockAccess world, BlockPos pos) {
        return getBlockState(world, pos).getBlock();
    }

    public static TileEntity getBlockTile(IBlockAccess world, BlockPos pos) {
        return world.getTileEntity(pos);
    }

    public static Material getBlockMaterial(IBlockAccess world, BlockPos pos) {
        return getBlock(world, pos).getMaterial();
    }

    @Deprecated
    public static Block getBlockOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getBlockState(world, pos.offset(side)).getBlock();
    }

    public static boolean isBlockLoaded(World world, BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    public static boolean isAreaLoaded(World world, BlockPos pos1, BlockPos pos2) {
        return world.isAreaLoaded(pos1, pos2);
    }

    public static boolean isBlockAir(IBlockAccess world, BlockPos pos, Block block) {
        return block.isAir(world, pos);
    }

    public static boolean isBlockAir(IBlockAccess world, BlockPos pos) {
        return world.isAirBlock(pos);
    }

    public static boolean isBlockAir(IBlockAccess world, BlockPos pos, IBlockState state) {
        return isBlockAir(world, pos, state.getBlock());
    }

    public static boolean isBlockAt(IBlockAccess world, BlockPos pos, Block block) {
        return block != null && block == getBlock(world, pos);
    }

    public static TileEntity getTileEntityOnSide(World world, BlockPos pos, EnumFacing side) {
        pos = pos.offset(side);
        if (isBlockLoaded(world, pos) && getBlock(world, pos) != Blocks.air)
            return getBlockTile(world, pos);
        return null;
    }

    public static TileEntity getTileEntityOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        pos = pos.offset(side);
        return world.getTileEntity(pos);
    }

    public static boolean setBlockState(World world, BlockPos pos, IBlockState blockState) {
        return world.setBlockState(pos, blockState);
    }

    public static boolean setBlockState(World world, BlockPos pos, IBlockState blockState, int update) {
        return world.setBlockState(pos, blockState, update);
    }

    public static boolean setBlockToAir(World world, BlockPos pos) {
        return world.setBlockToAir(pos);
    }

    public static void notifyBlocksOfNeighborChange(World world, BlockPos pos, Block block) {
        if (world != null && block != null)
            world.notifyNeighborsOfStateChange(pos, block);
    }

    public static void notifyBlocksOfNeighborChangeOnSide(World world, BlockPos pos, Block block, EnumFacing side) {
        pos = pos.offset(side);
        world.notifyNeighborsOfStateChange(pos, block);
    }

    public static void addBlockEvent(World world, BlockPos pos, Block block, int key, int value) {
        if (world != null && block != null)
            world.addBlockEvent(pos, block, key, value);
    }

    public static BlockPos findBlock(World world, BlockPos pos, int distance, Predicate<IBlockState> matcher) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        for (int yy = y - distance; yy < y + distance; yy++) {
            for (int xx = x - distance; xx < x + distance; xx++) {
                for (int zz = z - distance; zz < z + distance; zz++) {
                    BlockPos test = new BlockPos(xx, yy, zz);
                    if (matcher.apply(getBlockState(world, test)))
                        return test;
                }
            }
        }
        return null;
    }

}
