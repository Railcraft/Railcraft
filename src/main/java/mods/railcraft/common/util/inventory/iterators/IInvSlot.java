/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.iterators;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * This Interface represents an abstract inventory slot. It provides a unified interface for interfacing with Inventories.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IInvSlot {

    boolean canPutStackInSlot(ItemStack stack);

    boolean canTakeStackFromSlot(ItemStack stack);

    default boolean hasStack() {
        return !InvTools.isEmpty(getStack());
    }

    default boolean containsItem(Item item) {
        ItemStack stack = getStack();
        return !InvTools.isEmpty(stack) && stack.getItem() == item;
    }

    ItemStack decreaseStack();

    /**
     * It is not legal to edit the stack returned from this function.
     */
    ItemStack getStack();

//    void setStack(ItemStack stack);

    int getIndex();

}
