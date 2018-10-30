/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.items.ITrackItem;
import mods.railcraft.api.tracks.IItemTrack;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitSuspended;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class EntityCartTrackRelayer extends CartBaseMaintenancePattern {

    private static final int SLOT_STOCK = 0;
    private static final int SLOT_EXIST = 0;
    private static final int SLOT_REPLACE = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 1);

    public EntityCartTrackRelayer(World world) {
        super(world);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.MOW_TRACK_RELAYER;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(world))
            return;

        stockItems(SLOT_REPLACE, SLOT_STOCK);
        replace();
    }

    private void replace() {
        BlockPos guessPos = getPosition();
        IBlockState guessState = WorldPlugin.getBlockState(world, guessPos);
        if (!TrackTools.isRailBlock(guessState)) {
            guessPos = guessPos.down();
            guessState = WorldPlugin.getBlockState(world, guessPos);
            if (!TrackTools.isRailBlock(guessState)) {
                return;
            }
        }

        final BlockPos pos = guessPos;
        final IBlockState state = guessState;

        ItemStack trackExist = patternInv.getStackInSlot(SLOT_EXIST);
        ItemStack trackStock = getStackInSlot(SLOT_STOCK);

        boolean nextToSuspended = false;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            TileEntity tile = WorldPlugin.getBlockTile(world, pos.offset(side));
            if (tile instanceof IOutfittedTrackTile) {
                IOutfittedTrackTile track = (IOutfittedTrackTile) tile;
                if (track.getTrackKitInstance() instanceof TrackKitSuspended) {
                    nextToSuspended = true;
                    break;
                }
            }
        }

        if (nextToSuspended)
            return;

        if (InvTools.nonEmpty(trackExist) && InvTools.nonEmpty(trackStock))
            if (trackExist.getItem() instanceof ITrackItem) {
                ITrackItem trackItem = (ITrackItem) trackExist.getItem();
                if (trackItem.getPlacedBlock() == state.getBlock() && trackItem.isPlacedTileEntity(trackExist, WorldPlugin.getBlockTile(world, pos))) {
                    replaceTrack(pos, state);
                }
            } else if (trackExist.getItem() instanceof IItemTrack
                    && trackStock.getItem() instanceof IItemTrack
                    && TrackToolsAPI.getTrackType(trackExist) == TrackTools.getTrackTypeAt(world, pos, state)
                    && changeTrackType(pos, state, trackStock)) {
                // Track type replacements
            } else if (InvTools.getBlockStateFromStack(trackExist, world, pos) == state) {
                replaceTrack(pos, state);
            }
    }

    private boolean changeTrackType(BlockPos pos, IBlockState state, ItemStack trackStock) {
        if (state.getBlock() instanceof BlockTrackOutfitted) {
            TrackType targetType = TrackToolsAPI.getTrackType(trackStock);
            Optional<TileTrackOutfitted> optionalTrack = WorldPlugin.getTileEntity(world, pos, TileTrackOutfitted.class);
            if (!optionalTrack.isPresent()) {
                return false;
            }
            TileTrackOutfitted track = optionalTrack.get();
            if (!track.getTrackKitInstance().getTrackKit().isAllowedTrackType(targetType)) {
                return false;
            }
            // Drops
            NonNullList<ItemStack> list = NonNullList.create();
            track.getTrackType().getBaseBlock().getDrops(list, world, pos, state, 0);
            for (ItemStack stack : list) {
                CartToolsAPI.getTransferHelper().offerOrDropItem(this, stack);
            }

            track.setTrackType(targetType);
            decrStackSize(SLOT_STOCK, 1);
            blink();
            return true;
        }
        // Flex track
        replaceTrack(pos, state);
        return true;
    }

    private void replaceTrack(BlockPos pos, IBlockState state) {
        BlockRailBase.EnumRailDirection trackShape = removeOldTrack(pos, state);
        placeNewTrack(pos, SLOT_STOCK, trackShape);
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world))
            GuiHandler.openGui(EnumGui.CART_TRACK_RELAYER, player, world, this);
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        ItemStack trackReplace = patternInv.getStackInSlot(SLOT_REPLACE);
        return InvTools.isItemEqual(stack, trackReplace);
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_TRACK_RELAYER;
    }
}
