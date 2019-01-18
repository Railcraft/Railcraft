/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

            return tile.getSource().streamStacks().filter(StackFilters.anyMatch(tile.getItemFilters()))
                    .anyMatch(dest::willAccept);
        });

        modeHasWork.put(EnumTransferMode.TRANSFER, tile -> {
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());
            IInventoryComposite dest = tile.getDestination();

            return sourceManifest.values().stream()
                    .filter(entry -> dest.willAcceptAny(entry.stacks()))
                    .anyMatch(entry -> tile.transferredItems.count(entry.key()) < filterManifest.count(entry.key()));
        });

        modeHasWork.put(EnumTransferMode.STOCK, tile -> {
            IInventoryComposite dest = tile.getDestination();
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());
            InventoryManifest destManifest = InventoryManifest.create(dest, filterManifest.keySet());

            return sourceManifest.values().stream()
                    .filter(entry -> dest.willAcceptAny(entry.stacks()))
                    .anyMatch(entry -> destManifest.count(entry.key()) < filterManifest.count(entry.key()));
        });

        modeHasWork.put(EnumTransferMode.EXCESS, tile -> {
            IInventoryComposite dest = tile.getDestination();
            InventoryManifest filterManifest = InventoryManifest.create(tile.getItemFilters());
            InventoryManifest sourceManifest = InventoryManifest.create(tile.getSource(), filterManifest.keySet());

            if (filterManifest.values().stream().anyMatch(entry -> sourceManifest.count(entry.key()) > entry.count()))
                return true;

            InventoryManifest remainingManifest = InventoryManifest.create(tile.getSource());
            remainingManifest.keySet().removeIf(stackKey -> StackFilters.anyMatch(tile.getItemFilters()).test(stackKey.get()));

            return remainingManifest.streamValueStacks().anyMatch(dest::willAccept);
        });
    }

    protected final InventoryComposite chests = InventoryComposite.create();
    protected final Multiset<StackKey> transferredItems = HashMultiset.create();
    protected final InventoryMapper invBuffer;
    private final InventoryAdvanced invFilters = new InventoryAdvanced(9).callbackInv(this).phantom();
    private final MultiButtonController<EnumTransferMode> transferModeController = MultiButtonController.create(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());
    protected final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> !getClass().isInstance(tile), InventorySorter.SIZE_DESCENDING);

    TileItemManipulator() {
        setInventorySize(9);
        invBuffer = InventoryMapper.make(getInventory()).ignoreItemChecks();
    }

    public abstract IInventoryComposite getSource();

    public abstract IInventoryComposite getDestination();

    public MultiButtonController<EnumTransferMode> getTransferModeController() {
        return transferModeController;
    }

    public final InventoryAdvanced getItemFilters() {
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

        InventoryComposite cartInv = InventoryComposite.of(cart, getFacing().getOpposite());
        if (cartInv.isEmpty()) {
            sendCart(cart);
            return;
        }
        this.cart = cartInv;

        InventoryManifest filterManifest = InventoryManifest.create(getItemFilters());
        Stream<InventoryManifest.ManifestEntry> manifestStream = filterManifest.values().stream();
        switch (getMode()) {
            case ALL: {
                if (filterManifest.isEmpty()) {
                    ItemStack moved = getSource().moveOneItemTo(getDestination());
                    itemMoved(moved);
                } else {
                    moveItem(manifestStream);
                }
                break;
            }
            case TRANSFER: {
                moveItem(manifestStream.filter(entry -> transferredItems.count(entry.key()) < entry.count()));
                break;
            }
            case STOCK: {
                InventoryManifest destManifest = InventoryManifest.create(getDestination(), filterManifest.keySet());
                moveItem(manifestStream.filter(entry -> destManifest.count(entry.key()) < entry.count()));
                break;
            }
            case EXCESS: {
                InventoryManifest sourceManifest = InventoryManifest.create(getSource(), filterManifest.keySet());

                moveItem(manifestStream.filter(entry -> sourceManifest.count(entry.key()) > entry.count()));
                if (!isProcessing()) {
                    Predicate<ItemStack> canMove = StackFilters.anyMatch(filterManifest.keyStacks()).negate();

                    ItemStack moved = getSource().moveOneItemTo(getDestination(), canMove);
                    itemMoved(moved);
                }
                break;
            }
        }
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        IInventoryComposite cartInv = InventoryComposite.of(cart, getFacing().getOpposite());
        if (cartInv.slotCount() <= 0)
            return false;
        EnumRedstoneMode state = redstoneController().getButtonState();
        switch (state) {
            case IMMEDIATE:
                return false;
            case MANUAL:
                return true;
            case PARTIAL:
                if (cartInv.hasNoItems())
                    return true;
        }
        this.cart = cartInv;
        return modeHasWork.get(getMode()).test(this);
    }

    protected void moveItem(Stream<InventoryManifest.ManifestEntry> stream) {
        List<ItemStack> keys = stream.map(InventoryManifest.ManifestEntry::key).map(StackKey::get).collect(Collectors.toList());
        ItemStack moved = getSource().moveOneItemTo(getDestination(), StackFilters.anyMatch(keys));
        itemMoved(moved);
    }

    protected final void itemMoved(@Nullable ItemStack moved) {
        if (!InvTools.isEmpty(moved)) {
            setProcessing(true);
            transferredItems.add(StackKey.make(moved));
        }
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return InventoryComposite.of(cart, getFacing().getOpposite()).slotCount() > 0
                && super.canHandleCart(cart);
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
