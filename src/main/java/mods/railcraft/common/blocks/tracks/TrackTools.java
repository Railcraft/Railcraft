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
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
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

    public static boolean isStraightTrack(EnumRailDirection direction) {
        return isStraightHorizontalTrack(direction) || direction.isAscending();
    }

    public static boolean isStraightHorizontalTrack(EnumRailDirection direction) {
        return direction == EnumRailDirection.EAST_WEST || direction == EnumRailDirection.NORTH_SOUTH;
    }

    public static boolean isCornerTrack(EnumRailDirection direction) {
        return !isStraightTrack(direction);
    }

    public static boolean isEastWestTrack(EnumRailDirection direction) {
        return direction == EnumRailDirection.EAST_WEST || direction == EnumRailDirection.ASCENDING_EAST || direction == EnumRailDirection.ASCENDING_WEST;
    }

    public static boolean isNorthSouthTrack(EnumRailDirection direction) {
        return direction == EnumRailDirection.NORTH_SOUTH || direction == EnumRailDirection.ASCENDING_NORTH || direction == EnumRailDirection.ASCENDING_SOUTH;
    }

    public static boolean isStraightTrackAt(IBlockAccess world, BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (isRailBlock(block))
            return isStraightTrack(getTrackDirection(world, null, pos));
        return false;
    }

    public static boolean isRailBlock(Block block) {
        return block instanceof BlockRailBase;
    }

    public static boolean isRailBlock(IBlockState state) {
        return state.getBlock() instanceof BlockRailBase;
    }

    public static boolean isRailBlock(ItemStack stack) {
        Block block = InvTools.getBlockFromStack(stack);
        if (block == null)
            return false;
        return BlockRailBase.isRailBlock(block.getDefaultState());
    }

    public static boolean isRailItem(Item item) {
        if (item instanceof ITrackItem)
            return true;
        if (item instanceof ItemBlock)
            return BlockRailBase.isRailBlock(((ItemBlock) item).block.getDefaultState());
        return false;
    }
    
    public static EnumRailDirection getTrackDirection(IBlockAccess world, EntityMinecart cart, BlockPos pos) {
        return getTrackDirection(world, world.getBlockState(pos), cart, pos);
    }

    public static EnumRailDirection getTrackDirection(IBlockAccess world, IBlockState state, EntityMinecart cart, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof BlockRailBase) {
            BlockRailBase rail = (BlockRailBase) block;
            return rail.getRailDirection(world, pos, state, cart);
        }
        return EnumRailDirection.byMetadata(0);
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

    public static TileTrack placeTrack(TrackSpec track, World world, BlockPos pos, EnumRailDirection direction) {
        BlockTrack block = RailcraftBlocks.getBlockTrack();
        IBlockState state = block.getDefaultState().withProperty(BlockTrack.TRACK_DIRECTION, direction);
        WorldPlugin.setBlockState(world, pos, state);
        TileTrack tile = TrackFactory.makeTrackTile(track.createInstanceFromSpec());
        world.setTileEntity(pos, tile);
        return tile;
    }

}
