package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.plugins.forge.BlockPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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

        EnumRailDirection dir = EnumRailDirection.NORTH_SOUTH;
        if (travelDirection == EnumFacing.EAST || travelDirection == EnumFacing.WEST)
            dir = EnumRailDirection.EAST_WEST;
        if (!isValidReplacementBlock(pos) && isValidReplacementBlock(pos.up()) && (dir.isAscending() || dir == EnumRailDirection.NORTH_SOUTH || dir == EnumRailDirection.EAST_WEST))
            pos = pos.up();
        if (isValidReplacementBlock(pos) && isValidReplacementBlock(pos.down())) {
            pos = pos.down();
            if (travelDirection == EnumFacing.NORTH)
                dir = EnumRailDirection.ASCENDING_SOUTH;
            if (travelDirection == EnumFacing.SOUTH)
                dir = EnumRailDirection.ASCENDING_NORTH;
            if (travelDirection == EnumFacing.WEST)
                dir = EnumRailDirection.ASCENDING_EAST;
            if (travelDirection == EnumFacing.EAST)
                dir = EnumRailDirection.ASCENDING_WEST;
        }

        if (isValidNewTrackPosition(pos)) {
            IBlockState targetState = WorldPlugin.getBlockState(worldObj, pos);
            if (placeNewTrack(pos, SLOT_STOCK, dir)) {
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
    public int[] getSlotsForFace(EnumFacing side) {
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

    @Override
    public String getGuiID() {
        // TODO Auto-generated method stub
        throw new AbstractMethodError("Implement this!");
        return null;
    }
}
