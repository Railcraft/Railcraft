/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

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
    public Iterator<IExtInvSlot> iterator() {
        return new Iterator<IExtInvSlot>() {
            final int[] slots = inv.getSlotsForFace(EnumFacing.DOWN);
            int index;

            @Override
            public boolean hasNext() {
                return slots != null && index < slots.length;
            }

            @Override
            public IExtInvSlot next() {
                return slot(slots[index++]);
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

    private class InvSlot extends StandardInventoryIterator.InvSlot {

        public InvSlot(int slot) {
            super(slot);
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.canInsertItem(slot, stack, EnumFacing.DOWN);
        }

        @Override
        public boolean canTakeStackFromSlot() {
            return inv.canExtractItem(slot, getStack(), EnumFacing.DOWN);
        }

    }
}
