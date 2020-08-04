/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import mods.railcraft.common.blocks.interfaces.IDropsInv;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by CovertJaguar on 11/18/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryImplementor extends IInventory, IInventoryComposite, IDropsInv {
    /**
     * Gets the standalone inventory that backs this implementor.
     *
     * @return the delegate
     */
    IInventory getInventory();

    /**
     * {@inheritDoc}
     *
     * <p>This method must be implemented without delegation as it is determined by factors
     * outside of the standalone inventory; standalone inventories call the callback
     * (usually the objects that hold these inventories) for this method.
     *
     * @param player the player to check
     * @return true if the player can use this inventory
     */
    @Override
    boolean isUsableByPlayer(EntityPlayer player);

    @Override
    default int getSizeInventory() {
        return getInventory().getSizeInventory();
    }

    @Override
    default boolean isEmpty() {
        return getInventory().isEmpty();
    }

    @Override
    default ItemStack getStackInSlot(int index) {
        return getInventory().getStackInSlot(index);
    }

    @Override
    default ItemStack decrStackSize(int index, int count) {
        return getInventory().decrStackSize(index, count);
    }

    @Override
    default ItemStack removeStackFromSlot(int index) {
        return getInventory().removeStackFromSlot(index);
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack stack) {
        getInventory().setInventorySlotContents(index, stack);
    }

    @Override
    default int getInventoryStackLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    default void markDirty() {
    }

    @Override
    default void openInventory(EntityPlayer player) {
    }

    @Override
    default void closeInventory(EntityPlayer player) {
    }

    @Override
    default boolean isItemValidForSlot(int index, ItemStack stack) {
        return getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    default int getField(int id) {
        return getInventory().getField(id);
    }

    @Override
    default void setField(int id, int value) {
        getInventory().setField(id, value);
    }

    @Override
    default int getFieldCount() {
        return getInventory().getFieldCount();
    }

    @Override
    default void clear() {
        getInventory().clear();
    }

    @Override
    default String getName() {
        return getInventory().getName();
    }

    @Override
    default boolean hasCustomName() {
        return getInventory().hasCustomName();
    }

    @Override
    default ITextComponent getDisplayName() {
        return getInventory().getDisplayName();
    }
}
