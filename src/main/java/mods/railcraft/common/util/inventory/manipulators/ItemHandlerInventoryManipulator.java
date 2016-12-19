/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.manipulators;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryManipulator extends InventoryManipulator<IInvSlot> {

    private final IItemHandler inv;

    ItemHandlerInventoryManipulator(IItemHandler inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return InventoryIterator.getForge(inv).iterator();
    }

    @Override
    protected ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        stack = stack.copy();
        List<IInvSlot> filledSlots = new ArrayList<>(inv.getSlots());
        List<IInvSlot> emptySlots = new ArrayList<>(inv.getSlots());
        for (IInvSlot slot : InventoryIterator.getForge(inv)) {
            if (slot.canPutStackInSlot(stack)) {
                if (slot.getStack().isEmpty())
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
            }
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, doAdd);
        injected = tryPut(emptySlots, stack, injected, doAdd);
        stack.shrink(injected);
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        return stack;
    }

    private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= stack.getCount())
            return injected;
        for (IInvSlot slot : slots) {
            ItemStack stackToInsert = stack.copy();
            int amountToInsert = stack.getCount() - injected;
            stackToInsert.setCount(amountToInsert);
            ItemStack remainder = inv.insertItem(slot.getIndex(), stackToInsert, !doAdd);
            if (remainder.isEmpty())
                return stack.getCount();
            injected += amountToInsert - remainder.getCount();
            if (injected >= stack.getCount())
                return injected;
        }
        return injected;
    }

    @Override
    protected List<ItemStack> removeItem(Predicate<ItemStack> filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<>();
        for (IInvSlot slot : this) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                ItemStack removed = inv.extractItem(slot.getIndex(), amountNeeded, !doRemove);
                if (!removed.isEmpty()) {
                    amountNeeded -= removed.getCount();
                    outputList.add(removed);
                }
            }
        }
        return outputList;
    }
}
