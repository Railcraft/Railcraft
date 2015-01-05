/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import java.util.Iterator;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryIterator implements Iterable<IInvSlot> {

    private final ISidedInventory inv;

    SidedInventoryIterator(ISidedInventory inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return new Iterator<IInvSlot>() {
            int[] slots = inv.getAccessibleSlotsFromSide(0);
            int index = 0;

            @Override
            public boolean hasNext() {
                return slots != null && index < slots.length;
            }

            @Override
            public IInvSlot next() {
                return new InvSlot(slots[index++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
    }

    private class InvSlot implements IInvSlot {

        private final int slot;

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
            return inv.canInsertItem(slot, stack, 0);
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return inv.canExtractItem(slot, stack, 0);
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
            return "SlotNum = " + slot + " Stack = " + stack == null ? "null" : getStackInSlot().toString();
        }

    }
}
