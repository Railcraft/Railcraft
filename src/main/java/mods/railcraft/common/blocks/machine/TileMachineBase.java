/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Random;

public abstract class TileMachineBase extends RailcraftTileEntity {
    private boolean checkedBlock = false;

    public abstract IEnumMachine getMachineType();

    @Override
    public String getLocalizationTag() {
        return getMachineType().getTag() + ".name";
    }

    @Override
    public final short getId() {
        return (short) getMachineType().ordinal();
    }

    public boolean canCreatureSpawn(EnumCreatureType type) {
        return true;
    }

    public ArrayList<ItemStack> getDrops(int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getMachineType().getItem());
        return items;
    }

    public ArrayList<ItemStack> getBlockDroppedSilkTouch(int fortune) {
        return getDrops(fortune);
    }

    public boolean canSilkHarvest(EntityPlayer player) {
        return false;
    }

    public void initFromItem(ItemStack stack) {
    }

    public void onBlockAdded() {
    }

    /**
     * Called before the block is removed.
     */
    public void onBlockRemoval() {
        if (this instanceof IInventory)
            InvTools.dropInventory(new InventoryMapper((IInventory) this), worldObj, xCoord, yCoord, zCoord);
    }

    public boolean blockActivated(EntityPlayer player, int side) {
        if (player.isSneaking())
            return false;
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null) {
            if (stack.getItem() instanceof IActivationBlockingItem)
                return false;
            if (stack.getItem() instanceof ITrackItem)
                return false;
            if (TrackTools.isRailItem(stack.getItem()))
                return false;
        }
        return openGui(player);
    }

    public boolean rotateBlock(ForgeDirection axis) {
        return false;
    }

    public ForgeDirection[] getValidRotations() {
        return ForgeDirection.VALID_DIRECTIONS;
    }

    public boolean isSideSolid(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj))
            return;

        // Check and fix invalid block ids
        if (!checkedBlock) {
            checkedBlock = true;

            if (!getMachineType().isAvaliable()) {
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                return;
            }

            if (getBlockType() != getMachineType().getBlock()) {
                Game.log(Level.INFO, "Updating Machine Tile Block: {0} {1}->{2}, [{3}, {4}, {5}]", getClass().getSimpleName(), getBlockType(), getMachineType().getBlock(), xCoord, yCoord, zCoord);
                worldObj.setBlock(xCoord, yCoord, zCoord, getMachineType().getBlock(), getId(), 3);
                validate();
                worldObj.setTileEntity(xCoord, yCoord, zCoord, this);
                updateContainingBlockInfo();
            }

            int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            if (getBlockType() != null && getClass() != ((BlockMachine) getBlockType()).getMachineProxy().getMachine(meta).getTileClass()) {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, getId(), 3);
                validate();
                worldObj.setTileEntity(xCoord, yCoord, zCoord, this);
                Game.log(Level.INFO, "Updating Machine Tile Metadata: {0} {1}->{2}, [{3}, {4}, {5}]", getClass().getSimpleName(), meta, getId(), xCoord, yCoord, zCoord);
                updateContainingBlockInfo();
            }
        }
    }

    public boolean openGui(EntityPlayer player) {
        return false;
    }

    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    public int getLightValue() {
        return 0;
    }

    public float getResistance(Entity exploder) {
        return 4.5f;
    }

    public float getHardness() {
        return 2.0f;
    }

    public boolean isPoweringTo(int side) {
        return false;
    }

    public boolean canConnectRedstone(int dir) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand) {
    }

    public int colorMultiplier() {
        return 16777215;
    }

    public boolean recolourBlock(int color) {
        return false;
    }

    public ConnectStyle connectsToPost(ForgeDirection side) {
        if (isSideSolid(side.getOpposite()))
            return ConnectStyle.TWO_THIN;
        return ConnectStyle.NONE;
    }
}
