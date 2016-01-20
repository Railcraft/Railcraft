/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    public boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
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
            InvTools.dropInventory(new InventoryMapper((IInventory) this), worldObj, getPos());
    }

    public boolean blockActivated(EntityPlayer player, EnumFacing side) {
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

    public boolean rotateBlock(EnumFacing axis) {
        return false;
    }

    public EnumFacing[] getValidRotations() {
        return EnumFacing.VALUES;
    }

    public boolean isSideSolid(EnumFacing side) {
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(worldObj))
            return;

        // Check and fix invalid block ids
        if (!checkedBlock) {
            checkedBlock = true;

            if (!getMachineType().isAvailable()) {
                worldObj.setBlockToAir(getPos());
                return;
            }

            if (getBlockType() != getMachineType().getBlock()) {
                Game.log(Level.INFO, "Updating Machine Tile Block: {0} {1}->{2}, [{3}]", getClass().getSimpleName(), getBlockType(), getMachineType().getBlock(), getPos());
                worldObj.setBlockState(getPos(), newState, 3);
                validate();
                worldObj.setTileEntity(getPos(), this);
                updateContainingBlockInfo();
            }

            int meta = worldObj.getBlockMetadata(getPos());
            if (getBlockType() != null && getClass() != ((BlockMachine) getBlockType()).getMachineProxy().getMachine(meta).getTileClass()) {
                worldObj.setBlockState(getPos(), getId(), 3);
                validate();
                worldObj.setTileEntity(getPos(), this);
                Game.log(Level.INFO, "Updating Machine Tile Metadata: {0} {1}->{2}, [{3}]", getClass().getSimpleName(), meta, getId(), getPos());
                updateContainingBlockInfo();
            }
        }
    }

    public boolean openGui(EntityPlayer player) {
        return false;
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

    public boolean isPoweringTo(EnumFacing side) {
        return false;
    }

    public boolean canConnectRedstone(EnumFacing dir) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand) {
    }

    public int colorMultiplier() {
        return 16777215;
    }

    public boolean recolourBlock(EnumDyeColor color) {
        return false;
    }

    public ConnectStyle connectsToPost(EnumFacing side) {
        if (isSideSolid(side.getOpposite()))
            return ConnectStyle.TWO_THIN;
        return ConnectStyle.NONE;
    }
}
