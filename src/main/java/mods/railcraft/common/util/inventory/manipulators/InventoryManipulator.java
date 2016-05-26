/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryManipulator<T extends IInvSlot> implements Iterable<T> {
    @Nonnull
    public static InventoryManipulator<IExtInvSlot> get(IInventory inv) {
        return new StandardInventoryManipulator(inv);
    }

    @Nonnull
    public static InventoryManipulator<IInvSlot> get(IItemHandler inv) {
        return new ItemHandlerInventoryManipulator(inv);
    }

    @Nonnull
    public static InventoryManipulator<? extends IInvSlot> get(IInventoryObject inv) {
        if (inv.getInventoryObject() instanceof IInventory)
            return new StandardInventoryManipulator((IInventory) inv.getInventoryObject());
        if (inv.getInventoryObject() instanceof IItemHandler)
            return new ItemHandlerInventoryManipulator((IItemHandler) inv.getInventoryObject());
        throw new RuntimeException("Invalid Inventory Object");
    }

    protected InventoryManipulator() {
    }

    public boolean canAddStack(ItemStack stack) {
        return tryAddStack(stack) == null;
    }

    @Nullable
    public ItemStack tryAddStack(ItemStack stack) {
        return addStack(stack, false);
    }

    /**
     * Attempt to add the stack to the inventory.
     *
     * @return The remainder
     */
    @Nullable
    public ItemStack addStack(ItemStack stack) {
        return addStack(stack, true);
    }

    @Nullable
    protected abstract ItemStack addStack(ItemStack stack, boolean doAdd);

    /**
     * Returns true if an item matching the filter can be removed from the
     * inventory.
     */
    public boolean canRemoveItem(IStackFilter filter) {
        return tryRemoveItem(filter) == null;
    }

    /**
     * Returns the item that would be returned if an item matching the filter
     * was removed. Does not modify the inventory.
     */
    @Nullable
    public ItemStack tryRemoveItem(IStackFilter filter) {
        for (IInvSlot slot : this) {
            ItemStack stack = slot.getStack();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
                ItemStack output = stack.copy();
                output.stackSize = 1;
                return output;
            }
        }
        return null;
    }

    /**
     * Removed an item matching the filter.
     */
    @Nullable
    public ItemStack removeItem(IStackFilter filter) {
        for (IInvSlot slot : this) {
            ItemStack stack = slot.getStack();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack))
                return slot.decreaseStack();
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

    @Nonnull
    public List<ItemStack> removeItems(IStackFilter filter, int maxAmount) {
        return removeItem(filter, maxAmount, true);
    }

    @Nonnull
    protected abstract List<ItemStack> removeItem(IStackFilter filter, int maxAmount, boolean doRemove);

    @Nullable
    public ItemStack moveItem(IInventoryObject dest, IStackFilter filter) {
        InventoryManipulator imDest = InventoryManipulator.get(dest);
        for (IInvSlot slot : this) {
            ItemStack stack = slot.getStack();
            if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
                stack = stack.copy();
                stack.stackSize = 1;
                stack = imDest.addStack(stack);
                if (stack == null)
                    return slot.decreaseStack();
            }
        }
        return null;
    }

}
