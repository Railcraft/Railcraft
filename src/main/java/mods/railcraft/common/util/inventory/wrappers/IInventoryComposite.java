/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import com.google.common.collect.Iterators;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Primary interface for inventories of all types.
 *
 * Supports treating multiple inventories as a single object.
 *
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryComposite extends Iterable<IInventoryAdapter> {

    @Override
    default Iterator<IInventoryAdapter> iterator() {
        if (this instanceof IInventoryAdapter)
            return Iterators.singletonIterator((IInventoryAdapter) this);
        return Collections.emptyIterator();
    }

    default int slotCount() {
        return stream().mapToInt(IInventoryAdapter::getNumSlots).sum();
    }

    default boolean hasItems() {
        return streamStacks().findAny().isPresent();
    }

    default boolean hasNoItems() {
        return !hasItems();
    }

    default boolean isFull() {
        return streamSlots().allMatch(IInvSlot::hasStack);
    }

    default boolean hasEmptySlot() {
        return !isFull();
    }

    /**
     * Counts the number of items.
     *
     * @return the number of items in the inventory
     */
    default int countItems() {
        return countItems(StandardStackFilters.ALL);
    }

    /**
     * Counts the number of items that match the filter.
     *
     * @param filter the Predicate to match against
     * @return the number of items in the inventory
     */
    default int countItems(Predicate<ItemStack> filter) {
        return streamStacks().filter(filter).mapToInt(InvTools::sizeOf).sum();
    }

    /**
     * Counts the number of items that match the filter.
     *
     * @param filters the items to match against
     * @return the number of items in the inventory
     */
    default int countItems(ItemStack... filters) {
        return countItems(StackFilters.anyOf(filters));
    }

    default int countStacks() {
        return countStacks(StandardStackFilters.ALL);
    }

    default int countStacks(Predicate<ItemStack> filter) {
        return (int) streamStacks().filter(filter).count();
    }

    /**
     * Returns true if the inventory contains any of the specified items.
     *
     * @param items The ItemStack to look for
     * @return true is exists
     */
    default boolean contains(ItemStack... items) {
        return contains(StackFilters.anyOf(items));

    }

    /**
     * Returns true if the inventory contains the specified item.
     *
     * @param filter The ItemStack to look for
     * @return true is exists
     */
    default boolean contains(Predicate<ItemStack> filter) {
        return streamStacks().anyMatch(filter);
    }

    default boolean numItemsMoreThan(int amount) {
        int count = 0;
        for (IInventoryAdapter inventoryObject : this) {
            for (IInvSlot slot : InventoryIterator.get(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!InvTools.isEmpty(stack))
                    count += InvTools.sizeOf(stack);
                if (count >= amount)
                    return true;
            }
        }
        return false;
    }

    default int countMaxItemStackSize() {
        return streamStacks().mapToInt(ItemStack::getMaxStackSize).sum();
    }

    /**
     * Checks if there is room for the ItemStack in the inventory.
     *
     * @param stack The ItemStack
     * @return true if room for stack
     */
    default boolean canFit(ItemStack stack) {
        return !InvTools.isEmpty(stack) && stream().anyMatch(inv -> {
            InventoryManipulator im = InventoryManipulator.get(inv);
            return im.canAddStack(stack);
        });
    }

    /**
     * Returns a single item from the inventory that matches the
     * filter, but does not remove it.
     *
     * @param filter the filter to match against
     * @return An ItemStack
     */
    default ItemStack findOne(Predicate<ItemStack> filter) {
        for (IInventoryAdapter inventoryObject : this) {
            InventoryManipulator im = InventoryManipulator.get(inventoryObject);
            ItemStack removed = im.tryRemoveItem(filter);
            if (!InvTools.isEmpty(removed))
                return removed;
        }
        return InvTools.emptyStack();
    }

    /**
     * Returns all items from the inventory that match the
     * filter, but does not remove them.
     * The resulting set will be populated with a single instance of each item type.
     *
     * @param filter EnumItemType to match against
     * @return A Set of ItemStacks
     */
    default Set<StackKey> findAll(Predicate<ItemStack> filter) {
        Set<StackKey> items = new HashSet<>();
        for (IInventoryAdapter inventoryObject : this) {
            for (IInvSlot slot : InventoryIterator.get(inventoryObject)) {
                ItemStack stack = slot.getStack();
                if (!InvTools.isEmpty(stack) && filter.test(stack)) {
                    stack = stack.copy();
                    InvTools.setSize(stack, 1);
                    items.add(StackKey.make(stack));
                }
            }
        }
        return items;
    }

    /**
     * Removes a specified number of items matching the filter, but only if the
     * operation can be completed. If the function returns false, the inventory
     * will not be modified.
     *
     * @param amount the amount of items to remove
     * @param filter the filter to match against
     * @return true if there are enough items that can be removed, false
     * otherwise.
     */
    default boolean removeItemsAbsolute(int amount, ItemStack... filter) {
        return removeItemsAbsolute(amount, StackFilters.anyOf(filter));
    }

    /**
     * Removes a specified number of items matching the filter, but only if the
     * operation can be completed. If the function returns false, the inventory
     * will not be modified.
     *
     * @param amount the amount of items to remove
     * @param filter the filter to match against
     * @return true if there are enough items that can be removed, false
     * otherwise.
     */
    default boolean removeItemsAbsolute(int amount, Predicate<ItemStack> filter) {
        for (IInventoryAdapter inventoryObject : this) {
            InventoryManipulator im = InventoryManipulator.get(inventoryObject);
            if (im.canRemoveItems(filter, amount)) {
                im.removeItems(filter, amount);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes and returns a single item from the inventory.
     *
     * @return An ItemStack
     */
    default ItemStack removeOneItem() {
        return removeOneItem(StandardStackFilters.ALL);
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param filter the filter to match against
     * @return An ItemStack
     */
    default ItemStack removeOneItem(ItemStack... filter) {
        return removeOneItem(StackFilters.anyOf(filter));
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param filter the filter to match against
     * @return An ItemStack
     */
    default ItemStack removeOneItem(Predicate<ItemStack> filter) {
        for (IInventoryAdapter inventoryObject : this) {
            InventoryManipulator im = InventoryManipulator.get(inventoryObject);
            ItemStack stack = im.removeItem(filter);
            if (!InvTools.isEmpty(stack))
                return stack;
        }
        return InvTools.emptyStack();
    }

    /**
     * Places an ItemStack in a destination Inventory. Will attempt to move as
     * much of the stack as possible, returning any remainder.
     *
     * @param stack The ItemStack to put in the inventory.
     * @return Null if itemStack was completely moved, a new itemStack with
     * remaining stackSize if part or none of the stack was moved.
     */
    default ItemStack addStack(ItemStack stack) {
        for (IInventoryAdapter inv : this) {
            InventoryManipulator im = InventoryManipulator.get(inv);
            stack = im.addStack(stack);
            if (InvTools.isEmpty(stack))
                return InvTools.emptyStack();
        }
        return stack;
    }

    /**
     * Checks if inventory will accept the ItemStack.
     *
     * @param stack The ItemStack
     * @return true if room for stack
     */
    default boolean willAccept(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        ItemStack newStack = InvTools.copyOne(stack);
        return streamSlots().anyMatch(slot -> slot.canPutStackInSlot(newStack));
    }

    /**
     * Checks if inventory will accept any item from the list.
     *
     * @param stacks The ItemStacks
     * @return true if room for stack
     */
    default boolean willAcceptAny(List<ItemStack> stacks) {
        return stacks.stream().anyMatch(this::willAccept);
    }

    default Stream<IInventoryAdapter> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<? extends IInvSlot> streamSlots() {
        return stream().flatMap(inv -> InventoryIterator.get(inv).stream());
    }

    default Stream<ItemStack> streamStacks() {
        return stream().flatMap(inv -> InventoryIterator.get(inv).streamStacks());
    }
}
