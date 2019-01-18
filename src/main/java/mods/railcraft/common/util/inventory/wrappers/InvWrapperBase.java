/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import mods.railcraft.common.util.inventory.IInventoryComposite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by CovertJaguar on 3/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InvWrapperBase implements IInventory, IInventoryComposite {

    private final IInventory inv;
    protected boolean checkItems;

    protected InvWrapperBase(IInventory inv) {
        this(inv, true);
    }

    protected InvWrapperBase(IInventory inv, boolean checkItems) {
        this.inv = inv;
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
    public ItemStack removeStackFromSlot(int slot) {
        return inv.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inv.setInventorySlotContents(slot, itemstack);
    }

    @Override
    public String getName() {
        return inv.getName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inv.getDisplayName();
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
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return inv.isUsableByPlayer(entityplayer);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inv.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inv.closeInventory(player);
    }

    @Override
    public boolean hasCustomName() {
        return inv.hasCustomName();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return !checkItems || inv.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField(int id) {
        return inv.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inv.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inv.getFieldCount();
    }

    @Override
    public void clear() {
        inv.clear();
    }

    public boolean checkItems() {
        return checkItems;
    }

    @Override
    public int slotCount() {
        return getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }
}

