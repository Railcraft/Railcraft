/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IItemHandlerImplementor extends IInventoryImplementor, IItemHandlerModifiable {
    @Override
    default int getSlots() {
        return getSizeInventory();
    }

    @Override
    default ItemStack getStackInSlot(int index) {
        return getInventory().getStackInSlot(index);
    }

    @Override
    default void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        getInventory().setInventorySlotContents(slot, stack);
    }

    @Nonnull
    @Override
    default ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isItemValidForSlot(slot, stack)) return stack;
        return InventoryIterator.get(getInventory()).slot(slot).addToSlot(stack, simulate ? InvOp.EXECUTE : InvOp.SIMULATE);
    }

    @Nonnull
    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        return InventoryIterator.get(getInventory()).slot(slot).removeFromSlot(amount, simulate ? InvOp.EXECUTE : InvOp.SIMULATE);
    }

    @Override
    default int getSlotLimit(int slot) {
        return getInventoryStackLimit();
    }

    @Override
    default boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return isItemValidForSlot(slot, stack);
    }
}
