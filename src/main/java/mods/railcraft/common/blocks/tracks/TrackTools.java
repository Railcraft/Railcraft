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
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.instances.TrackBaseRailcraft;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings({"WeakerAccess"})
public class TrackTools {
    public static final int TRAIN_LOCKDOWN_DELAY = 200;

    public static boolean isRailBlockAt(IBlockAccess world, BlockPos pos) {
        return isRailBlock(WorldPlugin.getBlock(world, pos));
    }

    public static boolean isStraightTrackAt(IBlockAccess world, BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        return isRailBlock(block) && TrackShapeHelper.isStraight(getTrackDirection(world, pos));
    }

    public static boolean isRailBlock(Block block) {
        return block instanceof BlockRailBase;
    }

    public static boolean isRailBlock(IBlockState state) {
        return state.getBlock() instanceof BlockRailBase;
    }

    public static boolean isRailBlock(ItemStack stack) {
        Block block = InvTools.getBlockFromStack(stack);
        return block instanceof BlockRailBase;
    }

    public static boolean isRailItem(Item item) {
        return item instanceof ITrackItem || (item instanceof ItemBlock && isRailBlock(((ItemBlock) item).getBlock()));
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, IBlockState state) {
        return getTrackDirection(world, pos, state, null);
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos) {
        return getTrackDirection(world, pos, (EntityMinecart) null);
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, EntityMinecart cart) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return getTrackDirection(world, pos, state, cart);
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, IBlockState state, EntityMinecart cart) {
        if (state.getBlock() instanceof BlockRailBase)
            return ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, cart);
        return null;
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirectionRaw(IBlockAccess world, BlockPos pos) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return getTrackDirectionRaw(state);
    }

    @Nullable
    public static BlockRailBase.EnumRailDirection getTrackDirectionRaw(IBlockState state) {
        IProperty<EnumRailDirection> prop = getRailDirectionProperty(state.getBlock());
        if (prop != null)
            return state.getValue(prop);
        return null;
    }

    @Nullable
    public static IProperty<EnumRailDirection> getRailDirectionProperty(Block block) {
        if (block instanceof BlockRailBase)
            return ((BlockRailBase) block).getShapeProperty();
        return null;
    }

    public static boolean setTrackDirection(World world, BlockPos pos, EnumRailDirection wanted) {
        IBlockState state = world.getBlockState(pos);
        IProperty<EnumRailDirection> prop = getRailDirectionProperty(state.getBlock());
        if (prop != null) {
            if (prop.getAllowedValues().contains(wanted)) {
                state = state.withProperty(prop, wanted);
                return world.setBlockState(pos, state);
            }
            return false;
        }
        return false;
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
        return track instanceof TrackBaseRailcraft && ((TrackBaseRailcraft) track).speedController instanceof SpeedControllerHighSpeed;
    }

    public static TileTrack placeTrack(TrackSpec track, World world, BlockPos pos, BlockRailBase.EnumRailDirection direction) {
        WorldPlugin.setBlockState(world, pos, TrackToolsAPI.makeTrackState(RailcraftBlocks.getBlockTrack(), direction));
        TileTrack tile = TrackFactory.makeTrackTile(track.createInstanceFromSpec());
        world.setTileEntity(pos, tile);
        return tile;
    }

}
