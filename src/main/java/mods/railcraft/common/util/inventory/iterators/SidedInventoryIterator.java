/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/
package mods.railcraft.common.util.inventory.iterators;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Iterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryIterator extends StandardInventoryIterator {

    private final ISidedInventory inv;

    protected SidedInventoryIterator(ISidedInventory inv) {
        super(inv);
        this.inv = inv;
    }

    @Override
    public Iterator<StandardInventoryIterator.InvSlot> iterator() {
        return new Iterator<StandardInventoryIterator.InvSlot>() {
            int[] slots = inv.getSlotsForFace(EnumFacing.DOWN);
            int index = 0;

            @Override
            public boolean hasNext() {
                return slots != null && index < slots.length;
            }

            @Override
            public InvSlot next() {
                return new InvSlot(slots[index++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
    }

    public class InvSlot extends StandardInventoryIterator.InvSlot implements IInvSlot {

        public InvSlot(int slot) {
            super(slot);
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.canInsertItem(slot, stack, EnumFacing.DOWN);
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return inv.canExtractItem(slot, stack, EnumFacing.DOWN);
        }

    }
}
