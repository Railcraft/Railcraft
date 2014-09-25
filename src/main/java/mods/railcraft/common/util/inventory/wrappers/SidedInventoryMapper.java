/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Wrapper class used to bake the side variable into the object itself instead
 * of passing it around to all the inventory tools.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryMapper implements ISidedInventory {

    private final ISidedInventory inv;
    private final int side;
    private boolean checkItems = true;

    public SidedInventoryMapper(ISidedInventory inv, ForgeDirection side) {
        this(inv, side, true);
    }

    public SidedInventoryMapper(ISidedInventory inv, ForgeDirection side, boolean checkItems) {
        this.inv = inv;
        this.side = side.ordinal();
        this.checkItems = checkItems;
    }

    public IInventory getBaseInventory() {
        return inv;
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return inv.decrStackSize(slot, amount);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inv.setInventorySlotContents(slot, itemstack);
    }

    @Override
    public String getInventoryName() {
        return inv.getInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inv.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return inv.isUseableByPlayer(entityplayer);
    }

    @Override
    public void openInventory() {
        inv.openInventory();
    }

    @Override
    public void closeInventory() {
        inv.closeInventory();
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inv.getStackInSlotOnClosing(slot);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inv.hasCustomInventoryName();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return !checkItems || inv.isItemValidForSlot(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int s) {
        return inv.getAccessibleSlotsFromSide(side);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int s) {
        return !checkItems || inv.canInsertItem(slot, stack, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int s) {
        return !checkItems || inv.canExtractItem(slot, stack, side);
    }

}
