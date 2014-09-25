/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.entity.player.EntityPlayer;

public abstract class TileMachineItem extends TileMachineBase implements IInventory {

    private StandaloneInventory inv;

    protected TileMachineItem() {
        inv = new StandaloneInventory(0, (IInventory) this);
    }

    protected TileMachineItem(int invSize) {
        inv = new StandaloneInventory(invSize, (IInventory) this);
    }

    protected void setInventorySize(int invSize) {
        this.inv = new StandaloneInventory(invSize, (IInventory) this);
    }

    protected StandaloneInventory getInventory() {
        return inv;
    }

    protected void dropItem(ItemStack stack) {
        InvTools.dropItem(stack, worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return inv.decrStackSize(i, j);
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inv.getStackInSlot(i);
    }

    @Override
    public String getInventoryName() {
        return getName();
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("Items", data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("Items", data);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inv.setInventorySlotContents(i, itemstack);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUseableByPlayerHelper(this, player);
    }

}
