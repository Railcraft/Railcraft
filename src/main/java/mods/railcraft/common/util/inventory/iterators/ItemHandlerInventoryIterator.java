/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.iterators;

import mods.railcraft.common.util.inventory.InvOp;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryIterator extends InventoryIterator<IInvSlot> {

    private final IItemHandler inv;

    protected ItemHandlerInventoryIterator(IItemHandler inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return new Iterator<IInvSlot>() {
            int slot;

            @Override
            public boolean hasNext() {
                return slot < inv.getSlots();
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

    private class InvSlot implements IInvSlot {

        protected final int slot;

        public InvSlot(int slot) {
            this.slot = slot;
        }

        @Override
        public int getIndex() {
            return slot;
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.isItemValid(slot, stack);
        }

        @Override
        public boolean canTakeStackFromSlot(ItemStack stack) {
            return !InvTools.isEmpty(removeFromSlot(1, InvOp.SIMULATE));
        }

        @Override
        public ItemStack decreaseStack() {
            return inv.extractItem(slot, 1, false);
        }

        @Override
        public ItemStack removeFromSlot(int amount, InvOp op) {
            return inv.extractItem(slot, amount, op == InvOp.SIMULATE);
        }

        @Override
        public ItemStack addToSlot(ItemStack stack, InvOp op) {
            return inv.insertItem(slot, stack, op == InvOp.SIMULATE);
        }

        @Override
        public ItemStack getStack() {
            return InvTools.makeSafe(inv.getStackInSlot(slot));
        }

        @Override
        public int maxStackSize() {
            return inv.getSlotLimit(getIndex());
        }
    }
}
