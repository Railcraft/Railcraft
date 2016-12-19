/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Iterator;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;

/**
 * Creates a standalone instance of IInventory.
 * <p/>
 * Useful for hiding parts of an inventory from outsiders.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandaloneInventory implements IInventory, Iterable<ItemStack>, IInventoryObject {

    @Nullable
    private final String name;
    @Nullable
    private final Callback callback;
    private final NonNullList<ItemStack> contents;

    public StandaloneInventory(int size, @Nullable String name, @Nullable IInventory callback) {
        this.name = name;
        contents = NonNullList.withSize(size, ItemStack.EMPTY);
        this.callback = callback == null ? null : new InventoryCallback(callback);
    }

    public StandaloneInventory(int size, @Nullable String name, @Nullable RailcraftTileEntity callback) {
        this.name = name;
        contents = NonNullList.withSize(size, ItemStack.EMPTY);
        this.callback = callback == null ? null : new TileCallback(callback);
    }

    public StandaloneInventory(int size, @Nullable String name, @Nullable Callback callback) {
        this.name = name;
        contents = NonNullList.withSize(size, ItemStack.EMPTY);
        this.callback = callback;
    }

    public StandaloneInventory(int size, @Nullable IInventory callback) {
        this(size, null, callback);
    }

    public StandaloneInventory(int size, @Nullable RailcraftTileEntity callback) {
        this(size, null, callback);
    }

    public StandaloneInventory(int size, @Nullable String name) {
        this(size, name, (RailcraftTileEntity) null);
    }

    public StandaloneInventory(int size) {
        this(size, null, (RailcraftTileEntity) null);
    }

    public Stream<ItemStack> stream() {
        return contents.stream();
    }

    @Override
    public Object getInventoryObject() {
        return this;
    }

    @Override
    public int getNumSlots() {
        return getSizeInventory();
    }

    @Override
    public int getSizeInventory() {
        return contents.size();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return contents.get(i);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = contents.get(index);
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        ItemStack ret = stack.splitStack(count);
        markDirty();
        return ret;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack ret = contents.set(index, ItemStack.EMPTY);
        markDirty();
        return ret;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        contents.set(i, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public boolean hasCustomName() {
        return name != null || (callback != null && callback.hasCustomName());
    }

    @Override
    public String getName() {
        if (name != null) {
            return LocalizationPlugin.translate(name);
        }
        if (callback != null && callback.hasCustomName()) {
            return callback.getName();
        }
        return invTypeName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    protected String invTypeName() {
        return "Standalone";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (callback != null) {
            callback.markDirty();
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return callback == null || callback.isUsableByPlayer(entityplayer);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (callback != null) {
            callback.openInventory(player);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (callback != null) {
            callback.closeInventory(player);
        }
    }

    public NonNullList<ItemStack> getContents() {
        return contents;
    }

    public void writeToNBT(String tag, NBTTagCompound data) {
        InvTools.writeInvToNBT(this, tag, data);
    }

    public void readFromNBT(String tag, NBTTagCompound data) {
        InvTools.readInvFromNBT(this, tag, data);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return contents.iterator();
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < contents.size(); i++) {
            contents.set(i, ItemStack.EMPTY);
        }
        markDirty();
    }

    @Override
    public boolean isEmpty() {
        return getSizeInventory() == 0;
    }

    public abstract static class Callback {

        public boolean isUsableByPlayer(EntityPlayer player) {
            return true;
        }

        public void openInventory(EntityPlayer player) {
        }

        public void closeInventory(EntityPlayer player) {
        }

        public void markDirty() {
        }

        public String getName() {
            return "Standalone";
        }

        public Boolean hasCustomName() {
            return false;
        }

    }

    private static class InventoryCallback extends Callback {

        private final IInventory inv;

        InventoryCallback(IInventory inv) {
            this.inv = inv;
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return inv.isUsableByPlayer(player);
        }

        @Override
        public void openInventory(EntityPlayer player) {
            inv.openInventory(player);
        }

        @Override
        public void closeInventory(EntityPlayer player) {
            inv.closeInventory(player);
        }

        @Override
        public void markDirty() {
            inv.markDirty();
        }

        @Override
        public String getName() {
            return inv.getName();
        }

        @Override
        public Boolean hasCustomName() {
            return inv.hasCustomName();
        }

    }

    private static class TileCallback extends Callback {

        private final RailcraftTileEntity tile;

        TileCallback(RailcraftTileEntity tile) {
            this.tile = tile;
        }

        @Override
        public void markDirty() {
            tile.markDirty();
        }

        @Override
        public String getName() {
            return tile.getName();
        }

        @Override
        public Boolean hasCustomName() {
            return true;
        }
    }
}
