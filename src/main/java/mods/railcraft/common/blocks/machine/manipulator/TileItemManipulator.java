/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileItemManipulator extends TileManipulatorCart {

    protected static final EnumMap<EnumTransferMode, Predicate<TileItemManipulator>> modeHasWork = new EnumMap<>(EnumTransferMode.class);
    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 9);
    protected IInventoryObject cart;

    static {
        modeHasWork.put(EnumTransferMode.ALL, tile -> {
            Map<StackKey, Integer> sourceManifest = InvTools.createManifest(tile.getSource());
            Map<StackKey, Integer> filterManifest = InvTools.createManifest(tile.getItemFilters());
            List<IInventoryObject> dest = tile.getDestination();

            if (!filterManifest.isEmpty())
                sourceManifest.keySet().retainAll(filterManifest.keySet());
            return !sourceManifest.isEmpty() && sourceManifest.entrySet().stream()
                    .anyMatch(entry -> InvTools.acceptsItemStack(entry.getKey().get(), dest));
        });

        modeHasWork.put(EnumTransferMode.TRANSFER, tile -> {
            Map<StackKey, Integer> sourceManifest = InvTools.createManifest(tile.getSource());
            Map<StackKey, Integer> filterManifest = InvTools.createManifest(tile.getItemFilters());
            List<IInventoryObject> dest = tile.getDestination();

            filterManifest.keySet().retainAll(sourceManifest.keySet());

            return filterManifest.entrySet().stream()
                    .filter(key -> InvTools.acceptsItemStack(key.getKey().get(), dest))
                    .anyMatch(entry -> tile.transferredItems.getOrDefault(entry.getKey(), 0) < entry.getValue());
        });

        modeHasWork.put(EnumTransferMode.STOCK, tile -> {
            List<IInventoryObject> dest = tile.getDestination();
            Map<StackKey, Integer> destManifest = InvTools.createManifest(dest);
            Map<StackKey, Integer> filterManifest = InvTools.createManifest(tile.getItemFilters());

            return filterManifest.entrySet().stream()
                    .filter(key -> InvTools.acceptsItemStack(key.getKey().get(), dest))
                    .anyMatch(entry -> destManifest.getOrDefault(entry.getKey(), 0) < entry.getValue());
        });

        modeHasWork.put(EnumTransferMode.EXCESS, tile -> {
            List<IInventoryObject> dest = tile.getDestination();
            Map<StackKey, Integer> sourceManifest = InvTools.createManifest(tile.getSource());
            Map<StackKey, Integer> filterManifest = InvTools.createManifest(tile.getItemFilters());

            if (filterManifest.entrySet().stream().anyMatch(entry -> sourceManifest.getOrDefault(entry.getKey(), 0) > entry.getValue()))
                return true;

            sourceManifest.keySet().removeAll(filterManifest.keySet());
            return !sourceManifest.isEmpty() && sourceManifest.entrySet().stream()
                    .anyMatch(entry -> InvTools.acceptsItemStack(entry.getKey().get(), dest));
        });
    }

    protected final LinkedList<IInventoryObject> chests = new LinkedList<IInventoryObject>();
    protected final Map<StackKey, Integer> transferredItems = CollectionTools.createItemStackMap();
    protected final InventoryMapper invBuffer;
    private final PhantomInventory invFilters = new PhantomInventory(9, this);
    private final Predicate<ItemStack> filters = StackFilters.containedIn(invFilters);
    private final MultiButtonController<EnumTransferMode> transferModeController = MultiButtonController.create(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());
    protected AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> !getClass().isInstance(tile), InventorySorter.SIZE_DESCENDING);

    TileItemManipulator() {
        setInventorySize(9);
        invBuffer = new InventoryMapper(getInventory(), false);
    }

    public abstract List<IInventoryObject> getSource();

    public abstract List<IInventoryObject> getDestination();

    public MultiButtonController<EnumTransferMode> getTransferModeController() {
        return transferModeController;
    }

    public final PhantomInventory getItemFilters() {
        return invFilters;
    }

    public final Predicate<ItemStack> getFilters() {
        return filters;
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
        chests.addAll(invCache.getAdjacentInventories());
        chests.addFirst(invBuffer);

        IInventoryObject cartInv = InvTools.getInventory(cart, getFacing().getOpposite());
        if (cartInv == null) {
            sendCart(cart);
            return;
        }
        this.cart = cartInv;

        switch (getMode()) {
            case TRANSFER: {
                Map<StackKey, Integer> filterManifest = InvTools.createManifest(getItemFilters());
                filterManifest.entrySet().stream()
                        .filter(entry -> transferredItems.getOrDefault(entry.getKey(), 0) < entry.getValue())
                        .anyMatch(entry -> {
                            ItemStack moved = InvTools.moveOneItem(getSource(), getDestination(), entry.getKey().get());
                            if (moved != null) {
                                setProcessing(true);
                                transferredItems.merge(entry.getKey(), 1, Integer::sum);
                                return true;
                            }
                            return false;
                        });
                break;
            }
            case STOCK: {
                Map<StackKey, Integer> filterManifest = InvTools.createManifest(getItemFilters());
                Map<StackKey, Integer> destManifest = InvTools.createManifest(getDestination());
                moveItem(filterManifest.entrySet().stream()
                        .filter(entry -> destManifest.getOrDefault(entry.getKey(), 0) < entry.getValue()));
                break;
            }
            case EXCESS: {
                Map<StackKey, Integer> filterManifest = InvTools.createManifest(getItemFilters());
                Map<StackKey, Integer> sourceManifest = InvTools.createManifest(getSource());

                moveItem(filterManifest.entrySet().stream()
                        .filter(entry -> sourceManifest.getOrDefault(entry.getKey(), 0) > entry.getValue()));
                if (!isProcessing()) {
                    sourceManifest.keySet().removeAll(filterManifest.keySet());
                    moveItem(sourceManifest.entrySet().stream());
                }
                break;
            }
            case ALL: {
                Map<StackKey, Integer> filterManifest = InvTools.createManifest(getItemFilters());
                if (filterManifest.isEmpty()) {
                    ItemStack moved = InvTools.moveOneItem(getSource(), getDestination());
                    itemMoved(moved);
                } else {
                    moveItem(filterManifest.entrySet().stream());
                }
                break;
            }
        }
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        IInventoryObject cartInv = InvTools.getInventory(cart, getFacing().getOpposite());
        if (cartInv == null || cartInv.getNumSlots() <= 0)
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

    protected void moveItem(Stream<Map.Entry<StackKey, Integer>> stream) {
        stream.anyMatch(entry -> {
            ItemStack moved = InvTools.moveOneItem(getSource(), getDestination(), entry.getKey().get());
            return itemMoved(moved);
        });
    }

    protected boolean itemMoved(@Nullable ItemStack stack) {
        if (stack != null) {
            setProcessing(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        IInventoryObject cartInv = InvTools.getInventory(cart, getFacing().getOpposite());
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
