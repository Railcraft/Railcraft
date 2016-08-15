/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits;

import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTrackOutfitted extends ItemBlockRailcraft implements ITrackItem {

    public ItemTrackOutfitted(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.track");
    }

//    @Override
//    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
//        return getTrackSpec(stack).getItemModel();
//    }

    @Nonnull
    public TrackKit getTrackKitSpec(ItemStack stack) {
        if (stack != null && stack.getItem() == this) {
            NBTTagCompound nbt = InvTools.getItemData(stack);
            if (nbt.hasKey(TrackKit.NBT_TAG))
                return TrackRegistry.getTrackKit(nbt.getString(TrackKit.NBT_TAG));
        }
        return TrackRegistry.getTrackKit("railcraft:default");
    }

    @Override
    public int getMetadata(int i) {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + getTrackKitSpec(stack).getName().replace(':', '.');
    }

    @Override
    public BlockTrackOutfitted getPlacedBlock() {
        return (BlockTrackOutfitted) RailcraftBlocks.TRACK.block();
    }

    @Override
    public boolean isPlacedTileEntity(ItemStack stack, TileEntity tile) {
        if (tile instanceof TileTrackOutfitted) {
            TileTrackOutfitted track = (TileTrackOutfitted) tile;
            if (track.getTrackKitInstance().getTrackKit() == getTrackKitSpec(stack))
                return true;
        }
        return false;
    }

    @Override
    public boolean placeTrack(ItemStack stack, @Nullable EntityPlayer player, World world, BlockPos pos, @Nullable EnumRailDirection trackShape) {
        return placeTrack(stack, player, world, pos, trackShape, EnumFacing.UP);
    }

    private boolean placeTrack(ItemStack stack, @Nullable EntityPlayer player, World world, BlockPos pos, @Nullable EnumRailDirection trackShape, EnumFacing face) {
        BlockTrackOutfitted blockTrack = getPlacedBlock();
        if (blockTrack == null)
            return false;
        if (pos.getY() >= world.getHeight() - 1)
            return false;
        if (stack == null || !(stack.getItem() instanceof ItemTrackOutfitted))
            return false;

        TrackKit spec = getTrackKitSpec(stack);
        TileTrackOutfitted tile = TrackTileFactory.makeTrackTile(spec);
        ITrackKitInstance track = tile.getTrackKitInstance();

        boolean canPlace = world.canBlockBePlaced(blockTrack, pos, true, face, null, stack);
        if (track instanceof ITrackKitCustomPlaced)
            canPlace &= ((ITrackKitCustomPlaced) track).canPlaceRailAt(world, pos);
        else
            canPlace &= world.isSideSolid(pos.down(), EnumFacing.UP);

        if (canPlace) {
            IBlockState wantedState = TrackToolsAPI.makeTrackState(blockTrack, trackShape);
            boolean placed = WorldPlugin.setBlockState(world, pos, wantedState);
            // System.out.println("Block placement attempted");
            if (placed) {
                if (world.getBlockState(pos).getBlock() == blockTrack) {
                    world.setTileEntity(pos, tile);
                    blockTrack.onBlockPlacedBy(world, pos, wantedState, player, stack);
                    world.notifyBlockUpdate(pos, wantedState, wantedState, 3);
                }
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                world.playSound(x, y, z,
                        blockTrack.getSoundType().getPlaceSound(),
                        SoundCategory.PLAYERS,
                        (blockTrack.getSoundType().getVolume() + 1.0F) / 2.0F,
                        blockTrack.getSoundType().getPitch() * 0.8F,
                        false);
            }
            return true;
        } else
            return false;
    }

    //TODO: Test placement at player feet
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        return placeTrack(stack, player, world, pos, null, side);
    }
}
