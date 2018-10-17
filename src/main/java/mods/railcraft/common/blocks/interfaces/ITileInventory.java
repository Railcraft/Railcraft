/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by cover on 10/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileInventory extends IInventory, IInventoryObject, ITileCompare, ICapabilityProvider, ITile {

    @Override
    default Object getBackingObject() {
        return tile();
    }

    IInventory getInventory();

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
        getInventory().markDirty();
    }

    @Override
    default boolean isUsableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(tile(), player);
    }

    @Override
    default void openInventory(EntityPlayer player) {
        getInventory().openInventory(player);
    }

    @Override
    default void closeInventory(EntityPlayer player) {
        getInventory().closeInventory(player);
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

    @Override
    default int getNumSlots() {
        return getSizeInventory();
    }

    default void dropItem(ItemStack stack) {
        TileEntity te = tile();
        InvTools.dropItem(stack, te.getWorld(), te.getPos());
    }

    @Override
    default int getComparatorInputOverride() {
        return Container.calcRedstoneFromInventory(this);
    }
}
