/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryComposite;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileItemManipulator extends TileManipulatorCart {

    protected static final EnumMap<EnumTransferMode, Predicate<TileItemManipulator>> modeHasWork = new EnumMap<>(EnumTransferMode.class);
    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 9);
    protected IInventoryComposite cart;

    static {
        modeHasWork.put(EnumTransferMode.ALL, tile -> {
            IInventoryComposite dest = tile.getDestination();

            return tile.getSource().stackStream().filter(StackFilters.matchesAny(tile.getItemFilters()))
                    .anyMatch(stack -> InvTools.acceptsItemStack(stack, dest));
        });

        modeHasWork.put(EnumTransferMode.TRANSFER, tile -> {
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());
            IInventoryComposite dest = tile.getDestination();

            return sourceManifest.values().stream()
                    .filter(entry -> InvTools.acceptsAnyItemStack(entry.stacks(), dest))
                    .anyMatch(entry -> tile.transferredItems.count(entry.key()) < filterManifest.count(entry.key()));
        });

        modeHasWork.put(EnumTransferMode.STOCK, tile -> {
            IInventoryComposite dest = tile.getDestination();
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());
            InventoryManifest destManifest = InventoryManifest.create(dest, filterManifest.keySet());

            return sourceManifest.values().stream()
                    .filter(entry -> InvTools.acceptsAnyItemStack(entry.stacks(), dest))
                    .anyMatch(entry -> destManifest.count(entry.key()) < filterManifest.count(entry.key()));
        });

        modeHasWork.put(EnumTransferMode.EXCESS, tile -> {
            IInventoryComposite dest = tile.getDestination();
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());

            if (filterManifest.values().stream().anyMatch(entry -> sourceManifest.count(entry.key()) > entry.count()))
                return true;

            InventoryManifest remainingManifest = InventoryManifest.create(tile.getSource());
            remainingManifest.keySet().removeIf(stackKey -> StackFilters.matchesAny(tile.getItemFilters()).test(stackKey.get()));

            return remainingManifest.stackStream().anyMatch(stack -> InvTools.acceptsItemStack(stack, dest));
        });
    }

    protected final InventoryComposite chests = InventoryComposite.make();
    protected final Multiset<StackKey> transferredItems = HashMultiset.create();
    protected final InventoryMapper invBuffer;
    private final PhantomInventory invFilters = new PhantomInventory(9, this);
    private final MultiButtonController<EnumTransferMode> transferModeController = MultiButtonController.create(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());
    protected AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> !getClass().isInstance(tile), InventorySorter.SIZE_DESCENDING);

    TileItemManipulator() {
        setInventorySize(9);
        invBuffer = InventoryMapper.make(getInventory(), false);
    }

    public abstract IInventoryComposite getSource();

    public abstract IInventoryComposite getDestination();

    public MultiButtonController<EnumTransferMode> getTransferModeController() {
        return transferModeController;
    }

    public final PhantomInventory getItemFilters() {
        return invFilters;
    }

    public abstract Slot getBufferSlot(int id, int x, int y);

    @Override
    protected void setPowered(boolean p) {
        if (!isSendCartGateAction() && redstoneController().getButtonState() == EnumRedstoneMode.MANUAL) {
            super.setPowered(false);
            return;
        }
        super.setPowered(p);
    }

    @Override
    protected void reset() {
        super.reset();
        transferredItems.clear();
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        chests.clear();
        chests.add(invBuffer);
        chests.addAll(invCache.getAdjacentInventories());

        InventoryComposite cartInv = InventoryComposite.of(InventoryFactory.get(cart, getFacing().getOpposite()));
        if (cartInv.isEmpty()) {
            sendCart(cart);
            return;
        }
        this.cart = cartInv;

        switch (getMode()) {
            case ALL: {
                InventoryManifest filterManifest = InventoryManifest.create(getItemFilters());
                if (filterManifest.isEmpty()) {
                    ItemStack moved = InvTools.moveOneItem(getSource(), getDestination());
                    itemMoved(moved, null);
                } else {
                    moveItem(filterManifest.values().stream(), false);
                }
                break;
            }
            case TRANSFER: {
                InventoryManifest filterManifest = InventoryManifest.create(getItemFilters());
                moveItem(filterManifest.values().stream().filter(entry -> transferredItems.count(entry.key()) < entry.count()), true);
                break;
            }
            case STOCK: {
                InventoryManifest filterManifest = InventoryManifest.create(getItemFilters());
                InventoryManifest destManifest = InventoryManifest.create(getDestination(), filterManifest.keySet());
                moveItem(filterManifest.values().stream().filter(entry -> destManifest.count(entry.key()) < entry.count()), false);
                break;
            }
            case EXCESS: {
                InventoryManifest filterManifest = InventoryManifest.create(getItemFilters());
                InventoryManifest sourceManifest = InventoryManifest.create(getSource(), filterManifest.keySet());

                moveItem(filterManifest.values().stream().filter(entry -> sourceManifest.count(entry.key()) > entry.count()), false);
                if (!isProcessing()) {
                    Predicate<ItemStack> keep = filterManifest.keySet().stream()
                            .map(e -> StackFilters.matches(e.get())).reduce(StackFilters.none(), Predicate::or);

                    ItemStack moved = InvTools.moveOneItemExcept(getSource(), getDestination(), keep);
                    itemMoved(moved, null);
                }
                break;
            }
        }
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        IInventoryComposite cartInv = InventoryComposite.of(InventoryFactory.get(cart, getFacing().getOpposite()));
        if (cartInv.slotCount() <= 0)
            return false;
        EnumRedstoneMode state = redstoneController().getButtonState();
        switch (state) {
            case IMMEDIATE:
                return false;
            case MANUAL:
                return true;
            case PARTIAL:
                if (InvTools.isInventoryEmpty(cartInv))
                    return true;
        }
        this.cart = cartInv;
        return modeHasWork.get(getMode()).test(this);
    }

    protected void moveItem(Stream<InventoryManifest.ManifestEntry> stream, boolean track) {
        //noinspection ResultOfMethodCallIgnored
        stream.anyMatch(entry -> {
            ItemStack moved = InvTools.moveOneItem(getSource(), getDestination(), StackFilters.matches(entry.key().get()));
            return itemMoved(moved, track ? entry.key() : null);
        });
    }

    protected boolean itemMoved(@Nullable ItemStack remaining, @Nullable StackKey key) {
        if (!InvTools.isEmpty(remaining)) {
            setProcessing(true);
            if (key != null)
                transferredItems.add(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        IInventoryObject cartInv = InventoryFactory.get(cart, getFacing().getOpposite());
        return cartInv != null && cartInv.getNumSlots() > 0 && super.canHandleCart(cart);
    }

    public final EnumTransferMode getMode() {
        return transferModeController.getButtonState();
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(transferModeController.getCurrentState());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        transferModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        super.writeGuiData(data);
        data.writeByte(transferModeController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        transferModeController.setCurrentState(data.readByte());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        transferModeController.writeToNBT(data, "mode");
        getItemFilters().writeToNBT("invFilters", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        transferModeController.readFromNBT(data, "mode");

        if (data.hasKey("filters")) {
            NBTTagCompound filters = data.getCompoundTag("filters");
            getItemFilters().readFromNBT("Items", filters);
        } else {
            getItemFilters().readFromNBT("invFilters", data);
        }
    }

}
