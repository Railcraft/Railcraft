/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.IInventoryAdapter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryManipulator<T extends IInvSlot> implements Iterable<T> {

    public static InventoryManipulator<IExtInvSlot> get(IInventory inv) {
        return new StandardInventoryManipulator(inv);
    }


    public static InventoryManipulator<IInvSlot> get(IItemHandler inv) {
        return new ItemHandlerInventoryManipulator(inv);
    }

    public static InventoryManipulator<? extends IInvSlot> get(IInventoryAdapter inv) {
        if (inv.getBackingObject() instanceof IInventory)
            return new StandardInventoryManipulator((IInventory) inv.getBackingObject());
        if (inv.getBackingObject() instanceof IItemHandler)
            return new ItemHandlerInventoryManipulator((IItemHandler) inv.getBackingObject());
        throw new RuntimeException("Invalid Inventory Object");
    }

    protected InventoryManipulator() {
    }

    public boolean canAddStack(ItemStack stack) {
        return isEmpty(tryAddStack(stack));
    }

    public ItemStack tryAddStack(ItemStack stack) {
        return addStack(stack, false);
    }

    /**
     * Attempt to add the stack to the inventory.
     *
     * @return The remainder
     */
    public ItemStack addStack(ItemStack stack) {
        return addStack(stack, true);
    }

    protected abstract ItemStack addStack(ItemStack stack, boolean doAdd);

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
    public ItemStack tryRemoveItem(Predicate<ItemStack> filter) {
        for (IInvSlot slot : this) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                ItemStack output = stack.copy();
                setSize(output, 1);
                return output;
            }
        }
        return emptyStack();
    }

    /**
     * Removed an item matching the filter.
     */
    public ItemStack removeItem(Predicate<ItemStack> filter) {
        for (IInvSlot slot : this) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack))
                return slot.decreaseStack();
        }
        return emptyStack();
    }

    public boolean canRemoveItems(Predicate<ItemStack> filter, int maxAmount) {
        List<ItemStack> outputList = removeItem(filter, maxAmount, false);
        int found = outputList.stream().mapToInt(InvTools::sizeOf).sum();
        return found == maxAmount;
    }

    public List<ItemStack> removeItems(Predicate<ItemStack> filter, int maxAmount) {
        return removeItem(filter, maxAmount, true);
    }

    protected abstract List<ItemStack> removeItem(Predicate<ItemStack> filter, int maxAmount, boolean doRemove);

    public ItemStack moveItem(IInventoryAdapter dest, Predicate<ItemStack> filter) {
        InventoryManipulator imDest = InventoryManipulator.get(dest);
        for (IInvSlot slot : this) {
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
