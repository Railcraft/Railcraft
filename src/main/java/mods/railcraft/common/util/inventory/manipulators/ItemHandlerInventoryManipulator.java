/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryManipulator extends InventoryManipulator<IInvSlot> {

    private final IItemHandler inv;

    protected ItemHandlerInventoryManipulator(IItemHandler inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return InventoryIterator.getIterable(inv).iterator();
    }

    @Override
    protected ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        stack = stack.copy();
        List<IInvSlot> filledSlots = new ArrayList<IInvSlot>(inv.getSlots());
        List<IInvSlot> emptySlots = new ArrayList<IInvSlot>(inv.getSlots());
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
            if (slot.canPutStackInSlot(stack)) {
                if (slot.getStack() == null)
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
            }
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, doAdd);
        injected = tryPut(emptySlots, stack, injected, doAdd);
        stack.stackSize -= injected;
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= stack.stackSize)
            return injected;
        for (IInvSlot slot : slots) {
            ItemStack stackToInsert = stack.copy();
            int amountToInsert = stack.stackSize - injected;
            stackToInsert.stackSize = amountToInsert;
            ItemStack remainder = inv.insertItem(slot.getIndex(), stackToInsert, !doAdd);
            if (remainder == null)
                return stack.stackSize;
            injected += amountToInsert - remainder.stackSize;
            if (injected >= stack.stackSize)
                return injected;
        }
        return injected;
    }

    @Override
    protected List<ItemStack> removeItem(IStackFilter filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<ItemStack>();
        for (IInvSlot slot : this) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStack();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
                ItemStack removed = inv.extractItem(slot.getIndex(), amountNeeded, !doRemove);
                if (removed != null) {
                    amountNeeded -= removed.stackSize;
                    outputList.add(removed);
                }
            }
        }
        return outputList;
    }
}
