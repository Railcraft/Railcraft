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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTools {
    public static final int TRAIN_LOCKDOWN_DELAY = 200;

    public static boolean isRailBlockAt(IBlockAccess world, int x, int y, int z) {
        if (y < 0 || y > world.getHeight())
            return false;
        return isRailBlock(WorldPlugin.getBlock(world, x, y, z));
    }

    public static boolean isStraightTrackAt(IBlockAccess world, int x, int y, int z) {
        Block block = WorldPlugin.getBlock(world, x, y, z);
        if (isRailBlock(block))
            return getTrackMetaEnum(world, block, null, x, y, z).isStraightTrack();
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

    public static int getTrackMeta(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
        return getTrackMeta(world, world.getBlock(x, y, z), cart, x, y, z);
    }

    public static int getTrackMeta(IBlockAccess world, Block block, EntityMinecart cart, int x, int y, int z) {
        return ((BlockRailBase) block).getBasicRailMetadata(world, cart, x, y, z);
    }

    public static EnumTrackMeta getTrackMetaEnum(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
        return EnumTrackMeta.fromMeta(getTrackMeta(world, cart, x, y, z));
    }

    public static EnumTrackMeta getTrackMetaEnum(IBlockAccess world, Block block, EntityMinecart cart, int x, int y, int z) {
        return EnumTrackMeta.fromMeta(getTrackMeta(world, block, cart, x, y, z));
    }

    public static ITrackInstance getTrackInstanceAt(IBlockAccess world, int x, int y, int z) {
        if (WorldPlugin.getBlock(world, x, y, z) != RailcraftBlocks.getBlockTrack())
            return null;
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance();
        return null;
    }

    public static boolean isTrackAt(IBlockAccess world, int x, int y, int z, EnumTrack track, Block block) {
        return isTrackSpecAt(world, x, y, z, track.getTrackSpec(), block);
    }

    public static boolean isTrackAt(IBlockAccess world, int x, int y, int z, EnumTrack track) {
        return isTrackSpecAt(world, x, y, z, track.getTrackSpec());
    }

    public static boolean isTrackSpecAt(IBlockAccess world, int x, int y, int z, TrackSpec trackSpec, Block block) {
        if (block != RailcraftBlocks.getBlockTrack())
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        return isTrackSpec(tile, trackSpec);
    }

    public static boolean isTrackSpecAt(IBlockAccess world, int x, int y, int z, TrackSpec trackSpec) {
        return isTrackSpecAt(world, x, y, z, trackSpec, WorldPlugin.getBlock(world, x, y, z));
    }

    public static boolean isTrackSpec(TileEntity tile, TrackSpec trackSpec) {
        return (tile instanceof TileTrack) && ((TileTrack) tile).getTrackInstance().getTrackSpec() == trackSpec;
    }

    public static boolean isHighSpeedTrackAt(IBlockAccess world, int x, int y, int z) {
        ITrackInstance track = getTrackInstanceAt(world, x, y, z);
        if (track instanceof TrackBaseRailcraft)
            return ((TrackBaseRailcraft) track).speedController instanceof SpeedControllerHighSpeed;
        return false;
    }

    public static TileTrack placeTrack(TrackSpec track, World world, int x, int y, int z, int meta) {
        WorldPlugin.setBlock(world, x, y, z, RailcraftBlocks.getBlockTrack(), meta);
        TileTrack tile = TrackFactory.makeTrackTile(track.createInstanceFromSpec());
        world.setTileEntity(x, y, z, tile);
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
    public static boolean areTracksConnectedAlongAxis(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
    public static TrackScan scanStraightTrackSection(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
//                if (world.blockExists(xx, yy, z1))
                if (isRailBlockAt(world, xx, yy, z1)) {
                } else if (isRailBlockAt(world, xx, yy - 1, z1)) {
                    yy--;
                    if (yy < minY)
                        minY = yy;
                } else if (isRailBlockAt(world, xx, yy + 1, z1)) {
                    yy++;
                    if (yy > maxY)
                        maxY = yy;
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
//                if (world.blockExists(x1, yy, zz))
                if (isRailBlockAt(world, x1, yy, zz)) {
                } else if (isRailBlockAt(world, x1, yy - 1, zz)) {
                    yy--;
                    if (yy < minY)
                        minY = yy;
                } else if (isRailBlockAt(world, x1, yy + 1, zz)) {
                    yy++;
                    if (yy > maxY)
                        maxY = yy;
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
            NOT_ALIGNED,
            PATH_NOT_FOUND;
        }
    }
}
