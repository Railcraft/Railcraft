/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import mods.railcraft.api.core.WorldCoordinate;

import static mods.railcraft.common.util.misc.MiscTools.*;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class WorldPlugin {

    public static Block getBlock(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    public static Block getBlock(IBlockAccess world, WorldCoordinate pos) {
        return world.getBlock(pos.x, pos.y, pos.z);
    }

    public static TileEntity getBlockTile(IBlockAccess world, int x, int y, int z) {
        return world.getTileEntity(x, y, z);
    }

    public static Block getBlockOnSide(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return world.getBlock(getXOnSide(x, side), getYOnSide(y, side), getZOnSide(z, side));
    }

    public static boolean isBlockAt(World world, int x, int y, int z, Block block, int meta) {
        if (getBlock(world, x, y, z) != block)
            return false;
        return meta == -1 || getBlockMetadata(world, x, y, z) == meta;
    }

    public static boolean blockExists(World world, int x, int y, int z) {
        return world.blockExists(x, y, z);
    }

    public static boolean blockIsAir(World world, int x, int y, int z, Block block) {
        return block.isAir(world, x, y, z);
    }

    public static boolean blockIsAir(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z);
    }

    public static boolean blockExistsOnSide(World world, int x, int y, int z, ForgeDirection side) {
        return world.blockExists(getXOnSide(x, side), getYOnSide(y, side), getZOnSide(z, side));
    }

    public static int getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    public static int getBlockMetadataOnSide(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        return world.getBlockMetadata(getXOnSide(i, side), getYOnSide(j, side), getZOnSide(k, side));
    }

    public static TileEntity getTileEntityOnSide(World world, int x, int y, int z, ForgeDirection side) {
        int sx = getXOnSide(x, side);
        int sy = getYOnSide(y, side);
        int sz = getZOnSide(z, side);
        if (blockExists(world, sx, sy, sz) && getBlock(world, sx, sy, sz) != Blocks.air)
            return getBlockTile(world, sx, sy, sz);
        return null;
    }

    public static TileEntity getTileEntityOnSide(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int sx = getXOnSide(x, side);
        int sy = getYOnSide(y, side);
        int sz = getZOnSide(z, side);
        return world.getTileEntity(sx, sy, sz);
    }

    public static boolean setBlock(World world, int x, int y, int z, Block block) {
        return world.setBlock(x, y, z, block);
    }

    public static boolean setBlock(World world, int x, int y, int z, Block block, int meta) {
        return world.setBlock(x, y, z, block, meta, 3);
    }

    public static boolean setBlock(World world, int x, int y, int z, Block block, int meta, int update) {
        return world.setBlock(x, y, z, block, meta, update);
    }

    public static boolean setBlockToAir(World world, int x, int y, int z) {
        return world.setBlockToAir(x, y, z);
    }

    public static void notifyBlocksOfNeighborChange(World world, int x, int y, int z, Block block) {
        if (world != null && block != null)
            world.notifyBlocksOfNeighborChange(x, y, z, block);
    }

    public static void notifyBlocksOfNeighborChangeOnSide(World world, int x, int y, int z, Block block, ForgeDirection side) {
        world.notifyBlocksOfNeighborChange(getXOnSide(x, side), getYOnSide(y, side), getZOnSide(z, side), block);
    }

    public static void addBlockEvent(World world, int x, int y, int z, Block block, int key, int value) {
        if (world != null && block != null)
            world.addBlockEvent(x, y, z, block, key, value);
    }

    public static double getDistanceSq(WorldCoordinate a, WorldCoordinate b) {
        double distX = a.x - b.x;
        double distY = a.y - b.y;
        double distZ = a.z - b.z;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public static double getDistanceSq(WorldCoordinate a, double x, double y, double z) {
        double distX = a.x - x;
        double distY = a.y - y;
        double distZ = a.z - z;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public static WorldCoordinate findBlock(World world, int x, int y, int z, int distance, Block block, int meta) {
        for (int yy = y - distance; yy < y + distance; yy++) {
            for (int xx = x - distance; xx < x + distance; xx++) {
                for (int zz = z - distance; zz < z + distance; zz++) {
                    if (block == getBlock(world, xx, yy, zz) && meta == getBlockMetadata(world, xx, yy, zz))
                        return new WorldCoordinate(world.provider.dimensionId, xx, yy, zz);
                }
            }
        }
        return null;
    }

}
