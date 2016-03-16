/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import com.google.common.collect.Iterators;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;

/**
 * Creates a standalone instance of IInventory.
 * <p/>
 * Useful for hiding parts of an inventory from outsiders.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandaloneInventory implements IInventory, Iterable<ItemStack> {

    private final String name;
    private final Callback callback;
    private final ItemStack[] contents;

    public StandaloneInventory(int size, String name, IInventory callback) {
        this.name = name;
        contents = new ItemStack[size];
        this.callback = callback == null ? null : new InventoryCallback(callback);
    }

    public StandaloneInventory(int size, String name, RailcraftTileEntity callback) {
        this.name = name;
        contents = new ItemStack[size];
        this.callback = callback == null ? null : new TileCallback(callback);
    }

    public StandaloneInventory(int size, String name, Callback callback) {
        this.name = name;
        contents = new ItemStack[size];
        this.callback = callback;
    }

    public StandaloneInventory(int size, IInventory callback) {
        this(size, null, callback);
    }

    public StandaloneInventory(int size, RailcraftTileEntity callback) {
        this(size, null, callback);
    }

    public StandaloneInventory(int size, String name) {
        this(size, name, (RailcraftTileEntity) null);
    }

    public StandaloneInventory(int size) {
        this(size, null, (RailcraftTileEntity) null);
    }

    @Override
    public int getSizeInventory() {
        return contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return contents[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (contents[i] != null) {
            if (contents[i].stackSize <= j) {
                ItemStack itemstack = contents[i];
                contents[i] = null;
                markDirty();
                return itemstack;
            }
            ItemStack itemStack1 = contents[i].splitStack(j);
            if (contents[i].stackSize <= 0) {
                contents[i] = null;
            }
            markDirty();
            return itemStack1;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = contents[index];
        contents[index] = null;
        markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        contents[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public boolean hasCustomName() {
        return name != null || callback.hasCustomName();
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
    public IChatComponent getDisplayName() {
        return new ChatComponentText(getName());
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
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return callback == null || callback.isUseableByPlayer(entityplayer);
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

    public ItemStack[] getContents() {
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
        return Iterators.forArray(contents);
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
        for (int i = 0; i < contents.length; i++) {
            contents[i] = null;
        }
        markDirty();
    }

    public static abstract class Callback {

        public boolean isUseableByPlayer(EntityPlayer player) {
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

        public InventoryCallback(IInventory inv) {
            this.inv = inv;
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return inv.isUseableByPlayer(player);
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

        public TileCallback(RailcraftTileEntity tile) {
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
