/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import java.util.List;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileMultiBlockInventory extends TileMultiBlock implements IInventory {
    protected final StandaloneInventory inv;
    private final String guiTag;

    public TileMultiBlockInventory(String guiTag, int invSize, List<? extends MultiBlockPattern> patterns) {
        super(patterns);
        inv = new StandaloneInventory(invSize, (IInventory) this);
        this.guiTag = guiTag;
    }

    protected void dropItem(ItemStack stack) {
        InvTools.dropItem(stack, worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        TileMultiBlockInventory mBlock = (TileMultiBlockInventory) getMasterBlock();
        if (mBlock != null)
            return mBlock.inv.decrStackSize(i, j);
        return null;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        TileMultiBlockInventory mBlock = (TileMultiBlockInventory) getMasterBlock();
        if (mBlock != null)
            return mBlock.inv.getStackInSlot(i);
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        TileMultiBlockInventory mBlock = (TileMultiBlockInventory) getMasterBlock();
        if (mBlock != null)
            mBlock.inv.setInventorySlotContents(i, itemstack);
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return isStructureValid();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("invStructure", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("invStructure", data);
    }

    @Override
    public String getInventoryName() {
        return getName();
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public String getName() {
        return LocalizationPlugin.translate(guiTag);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUseableByPlayerHelper(this, player);
    }
}
