package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;

public class EntityCartTrackLayer extends CartMaintenancePatternBase {

    public static final int SLOT_STOCK = 0;
    public static final int SLOT_REPLACE = 0;
    public static final int[] SLOTS = InvTools.buildSlotArray(0, 1);

    public EntityCartTrackLayer(World world) {
        super(world);
    }

    public EntityCartTrackLayer(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y + getYOffset(), z);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.TRACK_LAYER;
    }

    @Override
    protected void func_180460_a(BlockPos pos, IBlockState state) {
        super.func_180460_a(pos, state);
        if (Game.isNotHost(worldObj))
            return;

        stockItems(SLOT_REPLACE, SLOT_STOCK);
        updateTravelDirection(pos, state);
        if (travelDirection != null)
            placeTrack(pos);
    }

    private void placeTrack(BlockPos pos) {
        pos = pos.offset(travelDirection);

        EnumTrackMeta trackMeta = EnumTrackMeta.NORTH_SOUTH;
        if (travelDirection == EnumFacing.EAST || travelDirection == EnumFacing.WEST)
            trackMeta = EnumTrackMeta.EAST_WEST;
        if (!isValidReplacementBlock(pos) && isValidReplacementBlock(pos.up()) && trackMeta.isStraightTrack())
            pos = pos.up();
        if (isValidReplacementBlock(pos) && isValidReplacementBlock(pos.down())) {
            pos = pos.down();
            if (travelDirection == EnumFacing.NORTH)
                trackMeta = EnumTrackMeta.SOUTH_SLOPE;
            if (travelDirection == EnumFacing.SOUTH)
                trackMeta = EnumTrackMeta.NORTH_SLOPE;
            if (travelDirection == EnumFacing.WEST)
                trackMeta = EnumTrackMeta.EAST_SLOPE;
            if (travelDirection == EnumFacing.EAST)
                trackMeta = EnumTrackMeta.WEST_SLOPE;
        }

        if (isValidNewTrackPosition(pos)) {
            IBlockState targetState = WorldPlugin.getBlockState(worldObj, pos);
            if (placeNewTrack(pos, SLOT_STOCK, trackMeta)) {
                targetState.getBlock().dropBlockAsItem(worldObj, pos, targetState, 0);
            }
        }
    }

    private boolean isValidNewTrackPosition(BlockPos pos) {
        return isValidReplacementBlock(pos) && World.doesBlockHaveSolidTopSurface(worldObj, pos.down());
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
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return SLOTS;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_TRACK_LAYER, player, worldObj, this);
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        ItemStack trackReplace = patternInv.getStackInSlot(SLOT_REPLACE);
        return InvTools.isItemEqual(stack, trackReplace);
    }
}
