/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * This Interface represents an abstract inventory slot. It provides a unified interface for interfacing with Inventories.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IInvSlot {

    boolean canPutStackInSlot(ItemStack stack);

    boolean canTakeStackFromSlot();

    default boolean hasStack() {
        return !InvTools.isEmpty(getStack());
    }

    default boolean containsItem(Item item) {
        ItemStack stack = getStack();
        return !InvTools.isEmpty(stack) && stack.getItem() == item;
    }

    default boolean matches(Predicate<ItemStack> filter) {
        return filter.test(getStack());
    }

    /**
     * Removes a single item from an inventory slot and returns it in a new stack.
     */
    ItemStack decreaseStack();

    ItemStack removeFromSlot(int amount, InvOp op);

    /**
     * Add as much of the given ItemStack to the slot as possible.
     *
     * @return the remaining items that were not added
     */
    ItemStack addToSlot(ItemStack stack, InvOp op);

    /**
     * It is not legal to edit the stack returned from this function.
     */
    ItemStack getStack();

//    void setStack(ItemStack stack);

    int getIndex();

    int maxSlotStackSize();

    default int getMaxStackSize() {
        return Math.min(maxSlotStackSize(), getStack().getMaxStackSize());
    }
}
