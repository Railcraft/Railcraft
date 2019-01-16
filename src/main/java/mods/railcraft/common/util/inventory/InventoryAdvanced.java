/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.util.misc.Optionals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Creates a standalone instance of IInventory.
 * <p/>
 * Useful for hiding parts of an inventory from outsiders.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryAdvanced extends InventoryBasic implements IInventoryComposite {

    public static final InventoryAdvanced ZERO_SIZE_INV = new InventoryAdvanced(0);

    private @Nullable Callback callback;
    private int inventoryStackLimit = 64;

    public InventoryAdvanced(int size, @Nullable String name) {
        super(name == null ? "Standalone" : name, false, size);
    }

    public InventoryAdvanced(int size) {
        this(size, null);
    }

    public InventoryAdvanced callbackEntity(Entity callback) {
        return callback(new CallbackEntity(callback));
    }

    public InventoryAdvanced callbackInv(IInventory callback) {
        return callback(new CallbackInv(callback));
    }

    public InventoryAdvanced callbackTile(TileRailcraft callback) {
        return callback(new CallbackTile(() -> callback));
    }

    public InventoryAdvanced callbackTile(Supplier<TileRailcraft> callback) {
        return callback(new CallbackTile(callback));
    }

    public InventoryAdvanced callback(Callback callback) {
        this.callback = callback;
        addInventoryChangeListener(callback);
        return this;
    }

    public InventoryAdvanced callback(Object callback) {
        if (callback instanceof IInventory)
            return callbackInv((IInventory) callback);
        if (callback instanceof Entity)
            return callbackEntity((Entity) callback);
        if (callback instanceof TileRailcraft)
            return callbackTile((TileRailcraft) callback);
        return this;
    }

    public InventoryAdvanced phantom() {
        inventoryStackLimit = 127;
        return this;
    }

    @Override
    public int slotCount() {
        return getSizeInventory();
    }

    @Override
    public boolean hasCustomName() {
        return (callback != null && callback.hasCustomName()) || super.hasCustomName();
    }

    @Override
    public String getName() {
        if (callback != null && callback.hasCustomName()) {
            return callback.getName();
        }
        return super.getName();
    }

    public void setInventoryStackLimit(int inventoryStackLimit) {
        this.inventoryStackLimit = inventoryStackLimit;
    }

    @Override
    public int getInventoryStackLimit() {
        return inventoryStackLimit;
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

    public void writeToNBT(String tag, NBTTagCompound data) {
        InvTools.writeInvToNBT(this, tag, data);
    }

    public void readFromNBT(String tag, NBTTagCompound data) {
        NBTTagList list = data.getTagList(tag, 10);
        for (byte entry = 0; entry < list.tagCount(); entry++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(entry);
            int slot = itemTag.getByte(InvTools.TAG_SLOT);
            if (slot >= 0 && slot < getSizeInventory()) {
                ItemStack stack = InvTools.readItemFromNBT(itemTag);
                setInventorySlotContents(slot, stack);
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        markDirty();
    }

    @Override
    public Stream<ItemStack> streamStacks() {
        return inventoryContents.stream().filter(InvTools::nonEmpty);
    }

    public abstract static class Callback implements IInventoryChangedListener {

        public boolean isUsableByPlayer(EntityPlayer player) {
            return true;
        }

        public void openInventory(EntityPlayer player) {
        }

        public void closeInventory(EntityPlayer player) {
        }

        public String getName() {
            return "Standalone";
        }

        public Boolean hasCustomName() {
            return false;
        }

    }

    public static class CallbackInv extends Callback {

        private final IInventory inv;

        public CallbackInv(IInventory inv) {
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
        public void onInventoryChanged(IInventory invBasic) {
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

    public static class CallbackTile extends Callback {

        private final Supplier<TileRailcraft> tile;

        public CallbackTile(Supplier<TileRailcraft> tile) {
            this.tile = tile;
        }

        public Optional<TileRailcraft> tile() {
            return Optional.ofNullable(tile.get());
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return Optionals.test(tile(), t -> TileRailcraft.isUsableByPlayerHelper(t, player));
        }

        @Override
        public void onInventoryChanged(IInventory invBasic) {
            tile().ifPresent(TileEntity::markDirty);
        }

        @Override
        public String getName() {
            return tile().map(TileRailcraft::getName).orElse("");
        }

        @Override
        public Boolean hasCustomName() {
            return tile().map(TileRailcraft::hasCustomName).orElse(false);
        }
    }

    public static class CallbackEntity extends Callback {

        private final Entity entity;

        public CallbackEntity(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void onInventoryChanged(IInventory invBasic) { }

        @Override
        public String getName() {
            return entity.getName();
        }

        @Override
        public Boolean hasCustomName() {
            return entity.hasCustomName();
        }
    }
}
