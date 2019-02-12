/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

/**
 * This interface defines the standard inventory operations.
 *
 * Other classes may expand on these, but these are foundations that everything else is built on.
 *
 * This interface exists mainly to enforce a consistent naming scheme on these functions.
 *
 * Created by CovertJaguar on 12/9/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryManipulator {
    /**
     * Returns the number of slots the inventory contains.
     */
    int slotCount();

    /**
     * Attempt to add the stack to the inventory returning the remainder.
     *
     * If the entire stack was accepted, it returns an empty stack.
     *
     * @return The remainder
     */
    ItemStack addStack(ItemStack stack, InvOp op);

    /**
     * Removes up to maxAmount items in one slot matching the filter.
     */
    ItemStack removeStack(int maxAmount, Predicate<ItemStack> filter, InvOp op);

    /**
     * Attempts to remove as many items from across the entire inventory as possible up to maxAmount.
     *
     * If the stacks come from multiple slots, each stack will have its own list entry.
     */
    List<ItemStack> extractItems(int maxAmount, Predicate<ItemStack> filter, InvOp op);

    /**
     * Moves a single item matching the filter from this inventory to another.
     */
    ItemStack moveOneItemTo(IInventoryComposite dest, Predicate<ItemStack> filter);

    Stream<? extends IInvSlot> streamSlots();

    Stream<ItemStack> streamStacks();

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

    /**
     * Checks if there is room for the ItemStack in the inventory.
     *
     * @param stack The ItemStack
     * @return true if room for stack
     */
    default boolean canFit(ItemStack stack) {
        return isEmpty(addStack(stack, InvOp.SIMULATE));
    }

    /**
     * Attempt to add the stack to the inventory returning the remainder.
     *
     * If the entire stack was accepted, it returns an empty stack.
     *
     * @return The remainder
     */
    default ItemStack addStack(ItemStack stack) {
        return addStack(stack, InvOp.EXECUTE);
    }

    /**
     * Removed an item matching the filter.
     */
    default ItemStack removeOneItem(Predicate<ItemStack> filter) {
        return removeStack(1, filter, InvOp.EXECUTE);
    }

    /**
     * Removes and returns a single item from the inventory.
     *
     * @return An ItemStack
     */
    default ItemStack removeOneItem() {
        return removeOneItem(StackFilters.ALL);
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
     * Returns the item that would be returned if an item matching the filter
     * was removed. Does not modify the inventory.
     */
    default ItemStack findOne(Predicate<ItemStack> filter) {
        return removeStack(1, filter, InvOp.SIMULATE);
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param dest the destination inventory
     * @return null if nothing was moved, the stack moved otherwise
     */
    default ItemStack moveOneItemTo(IInventoryComposite dest) {
        return moveOneItemTo(dest, Predicates.alwaysTrue());
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

    default int countStacks() {
        return countStacks(StackFilters.ALL);
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
     * Counts the number of items that match the filter.
     *
     * @param filter the Predicate to match against
     * @return the number of items in the inventory
     */
    default int countItems(Predicate<ItemStack> filter) {
        return streamStacks().filter(filter).mapToInt(InvTools::sizeOf).sum();
    }

    /**
     * Counts the number of items.
     *
     * @return the number of items in the inventory
     */
    default int countItems() {
        return countItems(StackFilters.ALL);
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

    default int countMaxItemStackSize() {
        return streamStacks().mapToInt(ItemStack::getMaxStackSize).sum();
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
        return streamStacks().filter(filter).map(StackKey::make).collect(Collectors.toSet());
    }

    /**
     * @see Container#calcRedstoneFromInventory(IInventory)
     */
    default int calcRedstone() {
        double average = InvTools.calculateFullness(this);

        return MathHelper.floor(average * 14.0F) + (hasNoItems() ? 0 : 1);
    }
}
