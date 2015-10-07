/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.InventoryIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryManipulator {

    private final IInventory inv;

    public static InventoryManipulator get(IInventory inv) {
//        if (inv instanceof ISpecialInventory)
//            return new SpecialManipulator((ISpecialInventory) inv);
        return new InventoryManipulator(inv);
    }

    protected InventoryManipulator(IInventory inv) {
        this.inv = inv;
    }

    protected Iterable<IInvSlot> getSlots() {
        return InventoryIterator.getIterable(inv);
    }

    public boolean canAddStack(ItemStack stack) {
        return tryAddStack(stack) == null;
    }

    public ItemStack tryAddStack(ItemStack stack) {
        return addStack(stack, false);
    }

    /**
     * Attempt to add the stack to the inventory.
     *
     * @param stack
     * @return The remainder
     */
    public ItemStack addStack(ItemStack stack) {
        return addStack(stack, true);
    }

    private ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        stack = stack.copy();
        List<IInvSlot> filledSlots = new ArrayList<IInvSlot>(inv.getSizeInventory());
        List<IInvSlot> emptySlots = new ArrayList<IInvSlot>(inv.getSizeInventory());
        for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
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

    private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= stack.stackSize)
            return injected;
        for (IInvSlot slot : slots) {
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
     * @param slot
     * @param stack
     * @param available Amount we can move
     * @param doAdd
     * @return Return the number of items moved.
     */
    private int addToSlot(IInvSlot slot, ItemStack stack, int available, boolean doAdd) {
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

    /**
     * Returns true if an item matching the filter can be removed from the
     * inventory.
     *
     * @param filter
     * @return
     */
    public boolean canRemoveItem(IStackFilter filter) {
        return tryRemoveItem(filter) == null;
    }

    /**
     * Returns the item that would be returned if an item matching the filter
     * was removed. Does not modify the inventory.
     *
     * @param filter
     * @return
     */
    public ItemStack tryRemoveItem(IStackFilter filter) {
        for (IInvSlot slot : getSlots()) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
                ItemStack output = stack.copy();
                output.stackSize = 1;
                return output;
            }
        }
        return null;
    }

    /**
     * Removed an item matching the filter.
     *
     * @param filter
     * @return
     */
    public ItemStack removeItem(IStackFilter filter) {
        for (IInvSlot slot : getSlots()) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.matches(stack))
                return slot.decreaseStackInSlot();
        }
        return null;
    }

    public boolean canRemoveItems(IStackFilter filter, int maxAmount) {
        List<ItemStack> outputList = removeItem(filter, maxAmount, false);
        int found = 0;
        for (ItemStack stack : outputList) {
            found += stack.stackSize;
        }
        return found == maxAmount;
    }

    public List<ItemStack> removeItems(IStackFilter filter, int maxAmount) {
        return removeItem(filter, maxAmount, true);
    }

    private List<ItemStack> removeItem(IStackFilter filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<ItemStack>();
        for (IInvSlot slot : getSlots()) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
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

    public ItemStack moveItem(IInventory dest, IStackFilter filter) {
        InventoryManipulator imDest = InventoryManipulator.get(dest);
        for (IInvSlot slot : getSlots()) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
                stack = stack.copy();
                stack.stackSize = 1;
                stack = imDest.addStack(stack);
                if (stack == null)
                    return slot.decreaseStackInSlot();
            }
        }
        return null;
    }

}
