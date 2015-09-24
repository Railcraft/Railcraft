package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityCartTrackLayer extends CartMaintenancePatternBase {

    public static final int SLOT_STOCK = 0;
    public static final int SLOT_REPLACE = 0;
    public static final int[] SLOTS = InvTools.buildSlotArray(0, 1);

    public EntityCartTrackLayer(World world) {
        super(world);
    }

    public EntityCartTrackLayer(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.TRACK_LAYER;
    }

    @Override
    protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustment, Block trackBlock, int trackMeta) {
        super.func_145821_a(trackX, trackY, trackZ, maxSpeed, slopeAdjustment, trackBlock, trackMeta);
        if (Game.isNotHost(worldObj))
            return;

        stockItems(SLOT_REPLACE, SLOT_STOCK);
        updateTravelDirection(trackX, trackY, trackZ, trackMeta);
        if (travelDirection != ForgeDirection.UNKNOWN)
            placeTrack(trackX, trackY, trackZ);
    }

    private void placeTrack(int x, int y, int z) {
        x = x + travelDirection.offsetX;
        z = z + travelDirection.offsetZ;

        EnumTrackMeta trackMeta = EnumTrackMeta.NORTH_SOUTH;
        if (travelDirection == ForgeDirection.EAST || travelDirection == ForgeDirection.WEST)
            trackMeta = EnumTrackMeta.EAST_WEST;
        if (!isValidReplacementBlock(x, y, z) && isValidReplacementBlock(x, y + 1, z) && trackMeta.isStraightTrack())
            y++;
        if (isValidReplacementBlock(x, y, z) && isValidReplacementBlock(x, y - 1, z)) {
            y--;
            if (travelDirection == ForgeDirection.NORTH)
                trackMeta = EnumTrackMeta.SOUTH_SLOPE;
            if (travelDirection == ForgeDirection.SOUTH)
                trackMeta = EnumTrackMeta.NORTH_SLOPE;
            if (travelDirection == ForgeDirection.WEST)
                trackMeta = EnumTrackMeta.EAST_SLOPE;
            if (travelDirection == ForgeDirection.EAST)
                trackMeta = EnumTrackMeta.WEST_SLOPE;
        }

        if (isValidNewTrackPosition(x, y, z)) {
            Block block = worldObj.getBlock(x, y, z);
            int metadata = worldObj.getBlockMetadata(x, y, z);
            if (placeNewTrack(x, y, z, SLOT_STOCK, trackMeta.ordinal())) {
                block.dropBlockAsItem(worldObj, x, y, z, metadata, 0);
            }
        }
    }

    private boolean isValidNewTrackPosition(int x, int y, int z) {
        return isValidReplacementBlock(x, y, z) && World.doesBlockHaveSolidTopSurface(worldObj, x, y - 1, z);
    }

    private boolean isValidReplacementBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return (worldObj.isAirBlock(x, y, z) ||
                block instanceof IPlantable ||
                block instanceof IShearable ||
                EntityTunnelBore.replaceableBlocks.contains(block)) ||
                block.isReplaceable(worldObj, x, y, z);
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

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.TRACK_LAYER.getTag());
    }
}
