/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows you to deal with multiple inventories through a single interface.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
//TODO use fastutil stuff
public final class InventoryConcatenator implements IInventory {

    private final List<Integer> slotMap = new ArrayList<>();
    private final List<IInventory> invMap = new ArrayList<>();
    private final Set<IInventory> invSet = new HashSet<>();

    private InventoryConcatenator() {
    }

    public static InventoryConcatenator make() {
        return new InventoryConcatenator();
    }

    public InventoryConcatenator add(IInventory inv) {
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            slotMap.add(slot);
            invMap.add(inv);
            invSet.add(inv);
        }
        return this;
    }

    @Override
    public int getSizeInventory() {
        return slotMap.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return invMap.get(index).getStackInSlot(slotMap.get(index));
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        return invMap.get(index).decrStackSize(slotMap.get(index), amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return invMap.get(index).removeStackFromSlot(slotMap.get(index));
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        invMap.get(index).setInventorySlotContents(slotMap.get(index), stack);
    }

    @Override
    public void clear() {
        for (IInventory inv : invSet) {
            inv.clear();
        }
    }

    @Override
    public boolean isEmpty() {
        return invSet.stream().allMatch(IInventory::isEmpty);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return invMap.get(slot).isItemValidForSlot(slotMap.get(slot), stack);
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

}
