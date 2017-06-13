/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitSuspended;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class EntityCartTrackRelayer extends CartBaseMaintenancePattern {

    private static final int SLOT_STOCK = 0;
    private static final int SLOT_EXIST = 0;
    private static final int SLOT_REPLACE = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 1);

    public EntityCartTrackRelayer(World world) {
        super(world);
    }

    public EntityCartTrackRelayer(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.MOW_TRACK_RELAYER;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isClient(worldObj))
            return;

        stockItems(SLOT_REPLACE, SLOT_STOCK);
        replace();
    }

    private void replace() {
        BlockPos pos = getPosition();

        if (TrackTools.isRailBlockAt(worldObj, pos.down()))
            pos = pos.down();

        Block block = WorldPlugin.getBlock(worldObj, pos);

        if (TrackTools.isRailBlock(block)) {
            ItemStack trackExist = patternInv.getStackInSlot(SLOT_EXIST);
            ItemStack trackStock = getStackInSlot(SLOT_STOCK);

            boolean nextToSuspended = false;
            for (EnumFacing side : EnumSet.of(EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH)) {
                TileEntity tile = WorldPlugin.getBlockTile(worldObj, pos.offset(side));
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

            if (trackExist != null && trackStock != null)
                if (trackExist.getItem() instanceof ITrackItem) {
                    ITrackItem trackItem = (ITrackItem) trackExist.getItem();
                    if (trackItem.getPlacedBlock() == block) {
                        TileEntity tile = worldObj.getTileEntity(pos);
                        if (trackItem.isPlacedTileEntity(trackExist, tile)) {
                            BlockRailBase.EnumRailDirection trackShape = removeOldTrack(pos, block);
                            placeNewTrack(pos, SLOT_STOCK, trackShape);
                        }
                    }
                } else if (InvTools.isStackEqualToBlock(trackExist, block)) {
                    BlockRailBase.EnumRailDirection trackShape = removeOldTrack(pos, block);
                    placeNewTrack(pos, SLOT_STOCK, trackShape);
                }
        }
    }

    @Override
    public boolean doInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_TRACK_RELAYER, player, worldObj, this);
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

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_TRACK_RELAYER;
    }
}
