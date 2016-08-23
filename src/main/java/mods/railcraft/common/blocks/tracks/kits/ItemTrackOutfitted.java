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
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTrackOutfitted extends ItemTrack implements ITrackItem {

    public ItemTrackOutfitted(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int i) {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.railcraft." + TrackRegistry.TRACK_TYPE.get(stack).getName() + "." + TrackRegistry.TRACK_KIT.get(stack).getName();
    }

    @Override
    public BlockTrackOutfitted getPlacedBlock() {
        return (BlockTrackOutfitted) getBlock();
    }

    @Override
    public boolean isPlacedTileEntity(ItemStack stack, TileEntity tile) {
        if (tile instanceof TileTrackOutfitted) {
            TileTrackOutfitted track = (TileTrackOutfitted) tile;
            if (track.getTrackKitInstance().getTrackKit() == TrackRegistry.TRACK_KIT.get(stack))
                return true;
        }
        return false;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        TrackKit trackKit = TrackRegistry.TRACK_KIT.get(stack);
        newState = newState.withProperty(BlockTrackOutfitted.TICKING, trackKit.requiresTicks());
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
