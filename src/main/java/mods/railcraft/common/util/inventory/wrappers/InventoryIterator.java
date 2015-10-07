/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryIterator implements Iterable<IInvSlot> {

    public static InventoryIterator getIterable(IInventory inv) {
        if (inv instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv);
        return new InventoryIterator(inv);
    }

    private final IInventory inv;
    private final int invSize;

    protected InventoryIterator(IInventory inv) {
        this.inv = inv;
        this.invSize = inv.getSizeInventory();
    }

    public Iterable<IInvSlot> notNull() {
        List<IInvSlot> filledSlots = new ArrayList<IInvSlot>(invSize);
        for (IInvSlot slot : this) {
            if (slot.getStackInSlot() != null)
                filledSlots.add(slot);
        }
        return filledSlots;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return new Iterator<IInvSlot>() {
            int slot = 0;

            @Override
            public boolean hasNext() {
                return slot < invSize;
            }

            @Override
            public IInvSlot next() {
                return new InvSlot(slot++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
    }

    protected class InvSlot implements IInvSlot {

        protected final int slot;

        public InvSlot(int slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack getStackInSlot() {
            return inv.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(ItemStack stack) {
            inv.setInventorySlotContents(slot, stack);
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.isItemValidForSlot(slot, stack);
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack decreaseStackInSlot() {
            return inv.decrStackSize(slot, 1);
        }

        @Override
        public int getIndex() {
            return slot;
        }

        @Override
        public String toString() {
            ItemStack stack = getStackInSlot();
            return "SlotNum = " + slot + " Stack = " + (stack == null ? "null" : getStackInSlot().toString());
        }

    }
}
