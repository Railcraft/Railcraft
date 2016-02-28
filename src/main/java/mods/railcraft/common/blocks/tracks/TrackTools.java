/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTools {
    public static final int TRAIN_LOCKDOWN_DELAY = 200;

    public static boolean isRailBlockAt(IBlockAccess world, BlockPos pos) {
        return isRailBlock(WorldPlugin.getBlock(world, pos));
    }

    public static boolean isStraightTrackAt(IBlockAccess world, BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (isRailBlock(block))
            return getTrackMetaEnum(world, block, null, pos).isStraightTrack();
        return false;
    }

    public static boolean isRailBlock(Block block) {
        return block instanceof BlockRailBase;
    }

    public static boolean isRailBlock(ItemStack stack) {
        Block block = InvTools.getBlockFromStack(stack);
        if (block == null)
            return false;
        return BlockRailBase.func_150051_a(block);
    }

    public static boolean isRailItem(Item item) {
        if (item instanceof ITrackItem)
            return true;
        if (item instanceof ItemBlock)
            return BlockRailBase.func_150051_a(((ItemBlock) item).field_150939_a);
        return false;
    }

    public static int getTrackMeta(IBlockAccess world, EntityMinecart cart, BlockPos pos) {
        return getTrackMeta(world, world.getBlock(pos), cart, pos);
    }

    public static int getTrackMeta(IBlockAccess world, Block block, EntityMinecart cart, BlockPos pos) {
        return ((BlockRailBase) block).getBasicRailMetadata(world, cart, pos);
    }

    public static EnumTrackMeta getTrackMetaEnum(IBlockAccess world, EntityMinecart cart, BlockPos pos) {
        return EnumTrackMeta.fromMeta(getTrackMeta(world, cart, pos));
    }

    public static EnumTrackMeta getTrackMetaEnum(IBlockAccess world, Block block, EntityMinecart cart, BlockPos pos) {
        return EnumTrackMeta.fromMeta(getTrackMeta(world, block, cart, pos));
    }

    public static ITrackInstance getTrackInstanceAt(IBlockAccess world, BlockPos pos) {
        if (WorldPlugin.getBlock(world, pos) != RailcraftBlocks.getBlockTrack())
            return null;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance();
        return null;
    }

    public static boolean isTrackAt(IBlockAccess world, BlockPos pos, EnumTrack track, Block block) {
        return isTrackSpecAt(world, pos, track.getTrackSpec(), block);
    }

    public static boolean isTrackAt(IBlockAccess world, BlockPos pos, EnumTrack track) {
        return isTrackSpecAt(world, pos, track.getTrackSpec());
    }

    public static boolean isTrackSpecAt(IBlockAccess world, BlockPos pos, TrackSpec trackSpec, Block block) {
        if (block != RailcraftBlocks.getBlockTrack())
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return isTrackSpec(tile, trackSpec);
    }

    public static boolean isTrackSpecAt(IBlockAccess world, BlockPos pos, TrackSpec trackSpec) {
        return isTrackSpecAt(world, pos, trackSpec, WorldPlugin.getBlock(world, pos));
    }

    public static boolean isTrackSpec(TileEntity tile, TrackSpec trackSpec) {
        return (tile instanceof TileTrack) && ((TileTrack) tile).getTrackInstance().getTrackSpec() == trackSpec;
    }

    public static boolean isHighSpeedTrackAt(IBlockAccess world, BlockPos pos) {
        ITrackInstance track = getTrackInstanceAt(world, pos);
        if (track instanceof TrackBaseRailcraft)
            return ((TrackBaseRailcraft) track).speedController instanceof SpeedControllerHighSpeed;
        return false;
    }

    public static TileTrack placeTrack(TrackSpec track, World world, BlockPos pos, int meta) {
        WorldPlugin.setBlock(world, pos, RailcraftBlocks.getBlockTrack(), meta);
        TileTrack tile = TrackFactory.makeTrackTile(track.createInstanceFromSpec());
        world.setTileEntity(pos, tile);
        return tile;
    }

    /**
     * Verifies that two rails are connected to each other along a straight line
     * with no gaps or wanderings.
     *
     * @param world The World object
     * @param x1    x-Coord of Rail #1
     * @param y1    y-Coord of Rail #1
     * @param z1    z-Coord of Rail #1
     * @param x2    x-Coord of Rail #2
     * @param y2    y-Coord of Rail #2
     * @param z2    z-Coord of Rail #2
     * @return true if they are connected
     */
    public static boolean areTracksConnectedAlongAxis(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        return scanStraightTrackSection(world, x1, y1, z1, x2, y2, z2).result == TrackScan.Result.VALID;
    }

    /**
     * Verifies that two rails are connected to each other along a straight line
     * with no gaps or wanderings.
     * <p/>
     * Also records the min and max y values along the way.
     *
     * @param world The World object
     * @param x1    x-Coord of Rail #1
     * @param y1    y-Coord of Rail #1
     * @param z1    z-Coord of Rail #1
     * @param x2    x-Coord of Rail #2
     * @param y2    y-Coord of Rail #2
     * @param z2    z-Coord of Rail #2
     * @return TrackScan object with results
     */
    public static TrackScan scanStraightTrackSection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        if (x1 != x2 && z1 != z2)
            return new TrackScan(TrackScan.Result.NOT_ALIGNED, minY, maxY);
        if (x1 != x2) {
            int min;
            int max;
            int yy;
            if (x1 < x2) {
                min = x1;
                max = x2;
                yy = y1;
            } else {
                min = x2;
                max = x1;
                yy = y2;
            }
            for (int xx = min; xx <= max; xx++) {
//                if (world.isBlockLoaded(xx, yy, z1))
                if (isRailBlockAt(world, xx, yy, z1)) {
                } else if (isRailBlockAt(world, xx, yy - 1, z1)) {
                    yy--;
                    if (yy < minY)
                        minY = yy;
                } else if (isRailBlockAt(world, xx, yy + 1, z1)) {
                    yy++;
                    if (yy > maxY)
                        maxY = yy;
                } else if (!WorldPlugin.isBlockLoaded(world, xx, yy, z1)) {
                    return new TrackScan(TrackScan.Result.UNKNOWN, minY, maxY);
                } else
                    return new TrackScan(TrackScan.Result.PATH_NOT_FOUND, minY, maxY);
            }
        } else if (z1 != z2) {
            int min;
            int max;
            int yy;
            if (z1 < z2) {
                min = z1;
                max = z2;
                yy = y1;
            } else {
                min = z2;
                max = z1;
                yy = y2;
            }
            for (int zz = min; zz <= max; zz++) {
//                if (world.isBlockLoaded(x1, yy, zz))
                if (isRailBlockAt(world, x1, yy, zz)) {
                } else if (isRailBlockAt(world, x1, yy - 1, zz)) {
                    yy--;
                    if (yy < minY)
                        minY = yy;
                } else if (isRailBlockAt(world, x1, yy + 1, zz)) {
                    yy++;
                    if (yy > maxY)
                        maxY = yy;
                } else if (!WorldPlugin.isBlockLoaded(world, x1, yy, zz)) {
                    return new TrackScan(TrackScan.Result.UNKNOWN, minY, maxY);
                } else
                    return new TrackScan(TrackScan.Result.PATH_NOT_FOUND, minY, maxY);
            }
        }
        return new TrackScan(TrackScan.Result.VALID, minY, maxY);
    }

    public static class TrackScan {
        public final Result result;
        public final boolean areConnected;
        public final int minY, maxY;

        public TrackScan(Result result, int minY, int maxY) {
            this.result = result;
            this.areConnected = result == Result.VALID;
            this.minY = minY;
            this.maxY = maxY;
        }

        public enum Result {
            VALID,
            UNKNOWN,
            NOT_ALIGNED,
            PATH_NOT_FOUND
        }
    }
}
