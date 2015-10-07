/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryIterator extends InventoryIterator {

    private final ISidedInventory inv;

    protected SidedInventoryIterator(ISidedInventory inv) {
        super(inv);
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

    private class InvSlot extends InventoryIterator.InvSlot implements IInvSlot {

        public InvSlot(int slot) {
            super(slot);
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.canInsertItem(slot, stack, 0);
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return inv.canExtractItem(slot, stack, 0);
        }

    }
}
