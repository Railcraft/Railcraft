/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

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

    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, IBlockState state) {
        return getTrackDirection(world, pos, state, null);
    }

    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos) {
        return getTrackDirection(world, pos, (EntityMinecart) null);
    }

    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, @Nullable EntityMinecart cart) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return getTrackDirection(world, pos, state, cart);
    }

    @Nonnull
    public static BlockRailBase.EnumRailDirection getTrackDirection(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        if (state.getBlock() instanceof BlockRailBase)
            return ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, cart);
        throw new IllegalArgumentException("Block was not a track");
    }

    public static BlockRailBase.EnumRailDirection getTrackDirectionRaw(IBlockAccess world, BlockPos pos) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return getTrackDirectionRaw(state);
    }

    public static BlockRailBase.EnumRailDirection getTrackDirectionRaw(IBlockState state) {
        IProperty<EnumRailDirection> prop = getRailDirectionProperty(state.getBlock());
        return state.getValue(prop);
    }

    public static IProperty<EnumRailDirection> getRailDirectionProperty(Block block) {
        if (block instanceof BlockRailBase)
            return ((BlockRailBase) block).getShapeProperty();
        throw new IllegalArgumentException("Block was not a track");
    }

    public static boolean setTrackDirection(World world, BlockPos pos, EnumRailDirection wanted) {
        IBlockState state = world.getBlockState(pos);
        IProperty<EnumRailDirection> prop = getRailDirectionProperty(state.getBlock());
        if (prop.getAllowedValues().contains(wanted)) {
            state = state.withProperty(prop, wanted);
            return world.setBlockState(pos, state);
        }
        return false;
    }

    @Nullable
    public static ITrackInstance getTrackInstanceAt(IBlockAccess world, BlockPos pos) {
        if (!WorldPlugin.isBlockAt(world, pos, RailcraftBlocks.track.block()))
            return null;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrack)
            return ((TileTrack) tile).getTrackInstance();
        return null;
    }

    @Nullable
    public static <T extends ITrackInstance> T getTrackInstance(@Nullable TileEntity tile, Class<T> instanceClass) {
        if (tile instanceof TileTrack) {
            ITrackInstance trackInstance = ((TileTrack) tile).getTrackInstance();
            if (instanceClass.isAssignableFrom(trackInstance.getClass()))
                return instanceClass.cast(trackInstance);
        }
        return null;
    }

    public static boolean isTrackAt(IBlockAccess world, BlockPos pos, EnumTrack track, Block block) {
        return isTrackSpecAt(world, pos, track.getTrackSpec(), block);
    }

    public static boolean isTrackAt(IBlockAccess world, BlockPos pos, EnumTrack track) {
        return isTrackSpecAt(world, pos, track.getTrackSpec());
    }

    public static boolean isTrackSpecAt(IBlockAccess world, BlockPos pos, TrackSpec trackSpec, Block block) {
        if (!RailcraftBlocks.track.isEqual(block))
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

    public static boolean isTrackClassAt(IBlockAccess world, BlockPos pos, Class<? extends ITrackInstance> trackClass, Block block) {
        if (!RailcraftBlocks.track.isEqual(block))
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return isTrackClass(tile, trackClass);
    }

    public static boolean isTrackClassAt(IBlockAccess world, BlockPos pos, Class<? extends ITrackInstance> trackClass) {
        return isTrackClassAt(world, pos, trackClass, WorldPlugin.getBlock(world, pos));
    }

    public static boolean isTrackClass(TileEntity tile, Class<? extends ITrackInstance> trackClass) {
        return (tile instanceof TileTrack) && trackClass.isAssignableFrom(((TileTrack) tile).getTrackInstance().getClass());
    }

    public static Optional<TileTrack> placeTrack(TrackSpec track, World world, BlockPos pos, BlockRailBase.EnumRailDirection direction) {
        BlockTrack block = (BlockTrack) RailcraftBlocks.track.block();
        TileTrack tile = null;
        if (block != null) {
            WorldPlugin.setBlockState(world, pos, TrackToolsAPI.makeTrackState(block, direction));
            tile = TrackFactory.makeTrackTile(track);
            world.setTileEntity(pos, tile);
        }
        //noinspection ConstantConditions
        return Optional.ofNullable(tile);
    }

}
