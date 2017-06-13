/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

public class EntityCartTrackLayer extends CartBaseMaintenancePattern {

    public static final int SLOT_STOCK = 0;
    public static final int SLOT_REPLACE = 0;
    public static final int[] SLOTS = InvTools.buildSlotArray(0, 1);

    public EntityCartTrackLayer(World world) {
        super(world);
    }

    public EntityCartTrackLayer(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.MOW_TRACK_LAYER;
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        super.moveAlongTrack(pos, state);
        if (Game.isClient(worldObj))
            return;

        stockItems(SLOT_REPLACE, SLOT_STOCK);
        updateTravelDirection(pos, state);
        if (travelDirection != null)
            placeTrack(pos);
    }

    private void placeTrack(BlockPos pos) {
        pos = pos.offset(travelDirection);

        BlockRailBase.EnumRailDirection trackShape = NORTH_SOUTH;
        if (travelDirection == EnumFacing.EAST || travelDirection == EnumFacing.WEST)
            trackShape = EAST_WEST;
        if (!isValidReplacementBlock(pos) && isValidReplacementBlock(pos.up()) && TrackShapeHelper.isStraight(trackShape))
            pos = pos.up();
        if (isValidReplacementBlock(pos) && isValidReplacementBlock(pos.down())) {
            pos = pos.down();
            if (travelDirection == EnumFacing.NORTH)
                trackShape = ASCENDING_SOUTH;
            if (travelDirection == EnumFacing.SOUTH)
                trackShape = ASCENDING_NORTH;
            if (travelDirection == EnumFacing.WEST)
                trackShape = ASCENDING_WEST;
            if (travelDirection == EnumFacing.EAST)
                trackShape = ASCENDING_EAST;
        }

        if (isValidNewTrackPosition(pos)) {
            IBlockState targetState = WorldPlugin.getBlockState(worldObj, pos);
            if (placeNewTrack(pos, SLOT_STOCK, trackShape)) {
                targetState.getBlock().dropBlockAsItem(worldObj, pos, targetState, 0);
            }
        }
    }

    private boolean isValidNewTrackPosition(BlockPos pos) {
        return isValidReplacementBlock(pos) && worldObj.isSideSolid(pos.down(), EnumFacing.UP);
    }

    private boolean isValidReplacementBlock(BlockPos pos) {
        IBlockState state = WorldPlugin.getBlockState(worldObj, pos);
        Block block = state.getBlock();
        return (WorldPlugin.isBlockAir(worldObj, pos, state) ||
                block instanceof IPlantable ||
                block instanceof IShearable ||
                EntityTunnelBore.replaceableBlocks.contains(block)) ||
                block.isReplaceable(worldObj, pos);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean doInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_TRACK_LAYER, player, worldObj, this);
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        ItemStack trackReplace = patternInv.getStackInSlot(SLOT_REPLACE);
        return InvTools.isItemEqual(stack, trackReplace);
    }

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_TRACK_LAYER;
    }
}
