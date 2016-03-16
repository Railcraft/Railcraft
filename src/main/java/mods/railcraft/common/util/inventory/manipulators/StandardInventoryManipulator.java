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

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.iterators.StandardInventoryIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardInventoryManipulator extends InventoryManipulator<StandardInventoryIterator.InvSlot> {

    private final IInventory inv;

    protected StandardInventoryManipulator(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<StandardInventoryIterator.InvSlot> iterator() {
        return InventoryIterator.getIterable(inv).iterator();
    }

    protected ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        stack = stack.copy();
        List<StandardInventoryIterator.InvSlot> filledSlots = new ArrayList<StandardInventoryIterator.InvSlot>(inv.getSizeInventory());
        List<StandardInventoryIterator.InvSlot> emptySlots = new ArrayList<StandardInventoryIterator.InvSlot>(inv.getSizeInventory());
        for (StandardInventoryIterator.InvSlot slot : this) {
            if (slot.canPutStackInSlot(stack))
                if (slot.getStackInSlot() == null)
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, doAdd);
        injected = tryPut(emptySlots, stack, injected, doAdd);
        stack.stackSize -= injected;
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    private int tryPut(List<StandardInventoryIterator.InvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= stack.stackSize)
            return injected;
        for (StandardInventoryIterator.InvSlot slot : slots) {
            ItemStack stackInSlot = slot.getStackInSlot();
            if (stackInSlot == null || InvTools.isItemEqual(stackInSlot, stack)) {
                int used = addToSlot(slot, stack, stack.stackSize - injected, doAdd);
                if (used > 0) {
                    injected += used;
                    if (injected >= stack.stackSize)
                        return injected;
                }
            }
        }
        return injected;
    }

    /**
     * @param available Amount we can move
     * @return Return the number of items moved.
     */
    private int addToSlot(StandardInventoryIterator.InvSlot slot, ItemStack stack, int available, boolean doAdd) {
        int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());

        ItemStack stackInSlot = slot.getStackInSlot();
        if (stackInSlot == null) {
            int wanted = Math.min(available, max);
            if (doAdd) {
                stackInSlot = stack.copy();
                stackInSlot.stackSize = wanted;
                slot.setStackInSlot(stackInSlot);
            }
            return wanted;
        }

        if (!InvTools.isItemEqual(stack, stackInSlot))
            return 0;

        int wanted = max - stackInSlot.stackSize;
        if (wanted <= 0)
            return 0;

        if (wanted > available)
            wanted = available;

        if (doAdd) {
            stackInSlot.stackSize += wanted;
            slot.setStackInSlot(stackInSlot);
        }
        return wanted;
    }

    @Override
    protected List<ItemStack> removeItem(IStackFilter filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<ItemStack>();
        for (StandardInventoryIterator.InvSlot slot : this) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
                ItemStack output = stack.copy();
                if (output.stackSize >= amountNeeded) {
                    output.stackSize = amountNeeded;
                    if (doRemove) {
                        stack.stackSize -= amountNeeded;
                        if (stack.stackSize <= 0)
                            stack = null;
                        slot.setStackInSlot(stack);
                    }
                    amountNeeded = 0;
                    outputList.add(output);
                } else {
                    amountNeeded -= output.stackSize;
                    outputList.add(output);
                    if (doRemove)
                        slot.setStackInSlot(null);
                }
            }
        }
        return outputList;
    }

}
