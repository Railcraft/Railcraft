/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.iterators;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardInventoryIterator extends InventoryIterator<IExtInvSlot> {

    private final IInventory inv;
    private final int invSize;

    StandardInventoryIterator(IInventory inv) {
        this.inv = inv;
        this.invSize = inv.getSizeInventory();
    }

    @Override
    public Iterator<IExtInvSlot> iterator() {
        return new Iterator<IExtInvSlot>() {
            int slot = 0;

            @Override
            public boolean hasNext() {
                return slot < invSize;
            }

            @Override
            public IExtInvSlot next() {
                return new InvSlot(slot++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
    }

    class InvSlot implements IExtInvSlot {

        protected final int slot;

        InvSlot(int slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack getStack() {
            return inv.getStackInSlot(slot);
        }

        @Override
        public void setStack(ItemStack stack) {
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
        public ItemStack decreaseStack() {
            return inv.decrStackSize(slot, 1);
        }

        @Override
        public int getIndex() {
            return slot;
        }

        @Override
        public String toString() {
            ItemStack stack = getStack();
            return "SlotNum = " + slot + " Stack = " + (stack == null ? "null" : getStack().toString());
        }

    }
}
