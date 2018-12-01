/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.common.util.inventory.InvOp;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryAdapter;
import mods.railcraft.common.util.inventory.wrappers.InventoryAdaptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class InventoryManipulator {

    private final IInventoryAdapter inv;

    public static InventoryManipulator get(IInventory inv) {
        return get(InventoryAdaptor.get(inv));
    }

    public static InventoryManipulator get(IItemHandler inv) {
        return get(InventoryAdaptor.get(inv));
    }

    public static InventoryManipulator get(IInventoryAdapter inv) {
        return new InventoryManipulator(inv);
    }

    private InventoryManipulator(IInventoryAdapter inv) {
        this.inv = inv;
    }

    public boolean canAddStack(ItemStack stack) {
        return isEmpty(tryAddStack(stack));
    }

    public ItemStack tryAddStack(ItemStack stack) {
        return addStack(stack, InvOp.SIMULATE);
    }

    /**
     * Attempt to add the stack to the inventory.
     *
     * @return The remainder
     */
    public ItemStack addStack(ItemStack stack) {
        return addStack(stack, InvOp.EXECUTE);
    }

    private ItemStack addStack(ItemStack stack, InvOp op) {
        if (isEmpty(stack))
            return emptyStack();
        stack = stack.copy();
        List<IInvSlot> filledSlots = new ArrayList<>();
        List<IInvSlot> emptySlots = new ArrayList<>();
        for (IInvSlot slot : InventoryIterator.get(inv)) {
            if (slot.canPutStackInSlot(stack)) {
                if (isEmpty(slot.getStack()))
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
            }
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, op);
        injected = tryPut(emptySlots, stack, injected, op);
        decSize(stack, injected);
        if (isEmpty(stack))
            return emptyStack();
        return stack;
    }

    private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, InvOp op) {
        if (injected >= sizeOf(stack))
            return injected;
        for (IInvSlot slot : slots) {
            int amountToInsert = sizeOf(stack) - injected;
            ItemStack remainder = slot.addToSlot(copy(stack, amountToInsert), op);
            if (isEmpty(remainder))
                return sizeOf(stack);
            injected += amountToInsert - sizeOf(remainder);
            if (injected >= sizeOf(stack))
                return injected;
        }
        return injected;
    }

    /**
     * Returns true if an item matching the filter can be removed from the
     * inventory.
     */
    public boolean canRemoveItem(Predicate<ItemStack> filter) {
        return !isEmpty(tryRemoveItem(filter));
    }

    /**
     * Returns the item that would be returned if an item matching the filter
     * was removed. Does not modify the inventory.
     */
    public final ItemStack tryRemoveItem(Predicate<ItemStack> filter) {
        return removeItem(filter, 1, InvOp.SIMULATE);
    }

    /**
     * Removed an item matching the filter.
     */
    public final ItemStack removeItem(Predicate<ItemStack> filter) {
        return removeItem(filter, 1, InvOp.EXECUTE);
    }

    /**
     * Removed x items in one slot matching the filter.
     */
    public ItemStack removeItem(Predicate<ItemStack> filter, int maxAmount, InvOp op) {
        for (IInvSlot slot : InventoryIterator.get(inv)) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                return slot.removeFromSlot(maxAmount, op);
            }
        }
        return emptyStack();
    }

    public boolean canRemoveItems(Predicate<ItemStack> filter, int maxAmount) {
        List<ItemStack> outputList = removeItems(filter, maxAmount, InvOp.SIMULATE);
        int found = outputList.stream().mapToInt(InvTools::sizeOf).sum();
        return found == maxAmount;
    }

    public List<ItemStack> removeItems(Predicate<ItemStack> filter, int maxAmount) {
        return removeItems(filter, maxAmount, InvOp.EXECUTE);
    }

    public List<ItemStack> removeItems(Predicate<ItemStack> filter, int maxAmount, InvOp op) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<>();
        for (IInvSlot slot : InventoryIterator.get(inv)) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                ItemStack output = slot.removeFromSlot(amountNeeded, op);
                if (!isEmpty(output)) {
                    amountNeeded -= sizeOf(output);
                    outputList.add(output);
                }
            }
        }
        return outputList;
    }

    public ItemStack moveItem(IInventoryAdapter dest, Predicate<ItemStack> filter) {
        InventoryManipulator imDest = InventoryManipulator.get(dest);
        for (IInvSlot slot : InventoryIterator.get(inv)) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                stack = stack.copy();
                setSize(stack, 1);
                stack = imDest.addStack(stack);
                if (isEmpty(stack))
                    return slot.decreaseStack();
            }
        }
        return emptyStack();
    }

}
