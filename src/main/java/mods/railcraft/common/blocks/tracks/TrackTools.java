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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
import net.minecraft.world.IBlockAccess;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTools {

    public static final int TRAIN_LOCKDOWN_DELAY = 200;

    public static boolean isRailBlockAt(IBlockAccess world, int x, int y, int z) {
        return isRailBlock(WorldPlugin.getBlock(world, x, y, z));
    }

    public static boolean isStraightTrackAt(IBlockAccess world, int x, int y, int z) {
        if (isRailBlockAt(world, x, y, z))
            return EnumTrackMeta.fromMeta(WorldPlugin.getBlockMetadata(world, x, y, z)).isStraightTrack();
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

    public static int getTrackMeta(World world, EntityMinecart cart, int x, int y, int z) {
        return getTrackMeta(world, world.getBlock(x, y, z), cart, x, y, z);
    }

    public static int getTrackMeta(World world, Block block, EntityMinecart cart, int x, int y, int z) {
        return ((BlockRailBase) block).getBasicRailMetadata(world, cart, x, y, z);
    }

    public static ITrackInstance getTrackInstanceAt(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance();
        return null;
    }

    public static boolean isHighSpeedTrackAt(World world, int x, int y, int z) {
        ITrackInstance track = getTrackInstanceAt(world, x, y, z);
        if (track instanceof TrackBaseRailcraft)
            return ((TrackBaseRailcraft) track).speedController instanceof SpeedControllerHighSpeed;
        return false;
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
        if (y1 < 0 || y2 < 0)
            return false;
        if (x1 != x2 && z1 != z2)
            return false;
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
                } else if (isRailBlockAt(world, xx, yy - 1, z1))
                    yy--;
                else if (isRailBlockAt(world, xx, yy + 1, z1))
                    yy++;
                else
                    return false;
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
                } else if (isRailBlockAt(world, x1, yy - 1, zz))
                    yy--;
                else if (isRailBlockAt(world, x1, yy + 1, zz))
                    yy++;
                else
                    return false;
            }
        }
        return true;
    }

    public static TileTrack placeTrack(TrackSpec track, World world, int x, int y, int z, int meta) {
        WorldPlugin.setBlock(world, x, y, z, RailcraftBlocks.getBlockTrack(), meta);
        TileTrack tile = TrackFactory.makeTrackTile(track.createInstanceFromSpec());
        world.setTileEntity(x, y, z, tile);
        return tile;
    }

}
