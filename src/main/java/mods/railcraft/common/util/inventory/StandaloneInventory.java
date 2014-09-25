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
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

/**
 * Creates a standalone instance of IInventory.
 *
 * Useful for hiding parts of an inventory from outsiders.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandaloneInventory implements IInventory, Iterable<ItemStack> {

    private Callback callback;
    private final String name;
    private ItemStack contents[];

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
            ItemStack itemstack1 = contents[i].splitStack(j);
            if (contents[i].stackSize <= 0) {
                contents[i] = null;
            }
            markDirty();
            return itemstack1;
        } else {
            return null;
        }
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
    public String getInventoryName() {
        if (name != null) {
            return LocalizationPlugin.translate(name);
        }
        if (callback != null) {
            return callback.getInventoryName();
        }
        return invTypeName();
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
        if (callback != null) {
            return callback.isUseableByPlayer(entityplayer);
        }
        return true;
    }

    @Override
    public void openInventory() {
        if (callback != null) {
            callback.openInventory();
        }
    }

    @Override
    public void closeInventory() {
        if (callback != null) {
            callback.closeInventory();
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
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
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    public static abstract class Callback {

        public boolean isUseableByPlayer(EntityPlayer entityplayer) {
            return true;
        }

        public void openInventory() {
        }

        public void closeInventory() {
        }

        public void markDirty() {
        }

        public String getInventoryName() {
            return "Standalone";
        }

    }

    private static class InventoryCallback extends Callback {

        private final IInventory inv;

        public InventoryCallback(IInventory inv) {
            this.inv = inv;
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer entityplayer) {
            return inv.isUseableByPlayer(entityplayer);
        }

        @Override
        public void openInventory() {
            inv.openInventory();
        }

        @Override
        public void closeInventory() {
            inv.closeInventory();
        }

        @Override
        public void markDirty() {
            inv.markDirty();
        }

        @Override
        public String getInventoryName() {
            return inv.getInventoryName();
        }

    }

    private static class TileCallback extends Callback {

        private final RailcraftTileEntity inv;

        public TileCallback(RailcraftTileEntity inv) {
            this.inv = inv;
        }

        @Override
        public void markDirty() {
            inv.markDirty();
        }

        @Override
        public String getInventoryName() {
            return inv.getName();
        }

    }
}
