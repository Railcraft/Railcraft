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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTools {
    public static final int TRAIN_LOCKDOWN_DELAY = 200;

    public static boolean isRailBlockAt(IBlockAccess world, int x, int y, int z) {
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

    @Nullable
    public static <T extends ITrackInstance> T getTrackInstance(@Nullable TileEntity tile, @Nonnull Class<T> instanceClass) {
        if (tile instanceof TileTrack) {
            ITrackInstance trackInstance = ((TileTrack) tile).getTrackInstance();
            if (instanceClass.isAssignableFrom(trackInstance.getClass()))
                return instanceClass.cast(trackInstance);
        }
        return null;
    }

    public static boolean isTrackAt(IBlockAccess world, int x, int y, int z, @Nonnull EnumTrack track, Block block) {
        return isTrackSpecAt(world, x, y, z, track.getTrackSpec(), block);
    }

    public static boolean isTrackAt(IBlockAccess world, int x, int y, int z, @Nonnull EnumTrack track) {
        return isTrackSpecAt(world, x, y, z, track.getTrackSpec());
    }

    public static boolean isTrackSpecAt(IBlockAccess world, int x, int y, int z, @Nonnull TrackSpec trackSpec, Block block) {
        if (block != RailcraftBlocks.getBlockTrack())
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        return isTrackSpec(tile, trackSpec);
    }

    public static boolean isTrackSpecAt(IBlockAccess world, int x, int y, int z, @Nonnull TrackSpec trackSpec) {
        return isTrackSpecAt(world, x, y, z, trackSpec, WorldPlugin.getBlock(world, x, y, z));
    }

    public static boolean isTrackSpec(TileEntity tile, @Nonnull TrackSpec trackSpec) {
        return (tile instanceof TileTrack) && ((TileTrack) tile).getTrackInstance().getTrackSpec() == trackSpec;
    }

    public static boolean isTrackClassAt(IBlockAccess world, int x, int y, int z, @Nonnull Class<? extends ITrackInstance> trackClass, Block block) {
        if (block != RailcraftBlocks.getBlockTrack())
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
        return isTrackClass(tile, trackClass);
    }

    public static boolean isTrackClassAt(IBlockAccess world, int x, int y, int z, @Nonnull Class<? extends ITrackInstance> trackClass) {
        return isTrackClassAt(world, x, y, z, trackClass, WorldPlugin.getBlock(world, x, y, z));
    }

    public static boolean isTrackClass(TileEntity tile, @Nonnull Class<? extends ITrackInstance> trackClass) {
        return (tile instanceof TileTrack) && trackClass.isAssignableFrom(((TileTrack) tile).getTrackInstance().getClass());
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

}
