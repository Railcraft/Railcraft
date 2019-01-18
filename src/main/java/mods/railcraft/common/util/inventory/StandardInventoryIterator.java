/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardInventoryIterator extends InventoryIterator<IExtInvSlot> {

    private final IInventory inv;
    private final int invSize;

    protected StandardInventoryIterator(IInventory inv) {
        this.inv = inv;
        this.invSize = inv.getSizeInventory();
    }

    @Override
    public Iterator<IExtInvSlot> iterator() {
        return new Iterator<IExtInvSlot>() {
            int slot;

            @Override
            public boolean hasNext() {
                return slot < invSize;
            }

            @Override
            public IExtInvSlot next() {
                return slot(slot++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
    }

    @Override
    public IExtInvSlot slot(int index) {
        return new InvSlot(index);
    }

    class InvSlot implements IExtInvSlot {

        protected final int slot;

        public InvSlot(int slot) {
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
        public boolean canTakeStackFromSlot() {
            return true;
        }

        @Override
        public ItemStack decreaseStack() {
            return inv.decrStackSize(slot, 1);
        }

        @Override
        public ItemStack removeFromSlot(int amount, InvOp op) {
            if (op == InvOp.EXECUTE) {
                return inv.decrStackSize(slot, amount);
            }
            ItemStack stack = getStack();
            return InvTools.copy(stack, Math.min(amount, sizeOf(stack)));
        }

        @Override
        public ItemStack addToSlot(ItemStack stack, InvOp op) {
            int available = sizeOf(stack);
            if (available <= 0)
                return stack.copy();
            int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            int wanted = 0;

            ItemStack stackInSlot = getStack();
            if (InvTools.isEmpty(stackInSlot)) {
                wanted = Math.min(available, max);
                if (wanted > 0 && op == InvOp.EXECUTE) {
                    setStack(copy(stack, wanted));
                }
            } else if (InvTools.isItemEqual(stack, stackInSlot)) {
                wanted = Math.min(available, max - sizeOf(stackInSlot));
                if (wanted > 0 && op == InvOp.EXECUTE) {
                    setStack(incSize(stackInSlot, wanted));
                }
            }
            return copy(stack, available - wanted);
        }

        @Override
        public int getIndex() {
            return slot;
        }

        @Override
        public int maxSlotStackSize() {
            return inv.getInventoryStackLimit();
        }

        @Override
        public String toString() {
            return "SlotNum = " + slot + " Stack = " + InvTools.toString(getStack());
        }

    }
}
