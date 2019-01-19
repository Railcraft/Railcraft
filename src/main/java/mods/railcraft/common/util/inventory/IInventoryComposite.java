/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import com.google.common.collect.Iterators;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Primary interface for inventories of all types.
 *
 * Supports treating multiple inventories as a single object,
 * enabling one-to-one, many-to-many, many-to-one, and one-to-many interactions between inventories.
 *
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
// No iterable: some other mods may add iterable to minecarts or block entities!
public interface IInventoryComposite extends /*Iterable<InventoryAdaptor>,*/ IInventoryManipulator {

    /**
     * Each IInventoryComposite is comprised of a collection of InventoryAdaptor objects.
     *
     * This function provides an iterator for iterating over them.
     *
     * The default implementation assumes this interface is being implemented by an object that can be wrapped by
     * an InventoryAdaptor and will return a singleton iterator for that object.
     *
     * If the implementing object cannot be wrapped by an InventoryAdaptor, it will thrown an exception
     * and must override this function.
     *
     * @see InventoryAdaptor
     */
    default Iterator<InventoryAdaptor> adaptors() {
        return InventoryAdaptor.of(this)
                .map(Iterators::singletonIterator)
                .orElseThrow(UnsupportedOperationException::new);
    }

    default Iterable<InventoryAdaptor> iterable() {
        return this::adaptors;
    }

    @Override
    default int slotCount() {
        return stream().mapToInt(InventoryAdaptor::slotCount).sum();
    }

    /**
     * Attempts to move a single item from one inventory to another.
     *
     * @param dest   the destination inventory
     * @param filter Predicate to match against
     * @return null if nothing was moved, the stack moved otherwise
     */
    @Override
    default ItemStack moveOneItemTo(IInventoryComposite dest, Predicate<ItemStack> filter) {
        return stream().map(src -> src.moveOneItemTo(dest, filter))
                .filter(InvTools::nonEmpty)
                .findFirst().orElseGet(InvTools::emptyStack);
    }

    @Override
    default List<ItemStack> extractItems(int maxAmount, Predicate<ItemStack> filter, InvOp op) {
        int amountNeeded = maxAmount;
        List<ItemStack> stacks = new ArrayList<>();
        for (InventoryAdaptor inv : iterable()) {
            List<ItemStack> tempStacks = inv.extractItems(amountNeeded, filter, op);
            amountNeeded -= tempStacks.stream().mapToInt(InvTools::sizeOf).sum();
            stacks.addAll(tempStacks);
            if (amountNeeded <= 0)
                return stacks;
        }
        return stacks;
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
    default boolean removeItems(int amount, ItemStack... filter) {
        return removeItems(amount, StackFilters.anyOf(filter));
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
    default boolean removeItems(int amount, Predicate<ItemStack> filter) {
        if (InvTools.tryRemove(this, amount, filter, InvOp.SIMULATE))
            return InvTools.tryRemove(this, amount, filter, InvOp.EXECUTE);
        return false;
    }

    /**
     * Removed x items in one slot matching the filter.
     */
    @Override
    default ItemStack removeStack(int maxAmount, Predicate<ItemStack> filter, InvOp op) {
        return stream().map(inv -> inv.removeStack(maxAmount, filter, op))
                .filter(InvTools::nonEmpty)
                .findFirst().orElseGet(InvTools::emptyStack);
    }

    /**
     * Places an ItemStack in a destination Inventory. Will attempt to move as
     * much of the stack as possible, returning any remainder.
     *
     * @param stack The ItemStack to put in the inventory.
     * @return Null if itemStack was completely moved, a new itemStack with
     * remaining stackSize if part or none of the stack was moved.
     */
    @Override
    default ItemStack addStack(ItemStack stack, InvOp op) {
        for (InventoryAdaptor inv : iterable()) {
            stack = inv.addStack(stack, op);
            if (InvTools.isEmpty(stack))
                return InvTools.emptyStack();
        }
        return stack;
    }

    default Stream<InventoryAdaptor> stream() {
        return StreamSupport.stream(iterable().spliterator(), false);
    }

    @Override
    default Stream<? extends IInvSlot> streamSlots() {
        return stream().flatMap(InventoryAdaptor::streamSlots);
    }

    @Override
    default Stream<ItemStack> streamStacks() {
        return stream().flatMap(InventoryAdaptor::streamStacks);
    }
}
