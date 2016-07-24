/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.inventory.wrappers.InventoryObject;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileItemUnloader extends TileLoaderItemBase {

    private static final EnumRedstoneMode[] REDSTONE_MODES = {EnumRedstoneMode.IMMEDIATE, EnumRedstoneMode.COMPLETE, EnumRedstoneMode.MANUAL};
    private final InventoryMapper invBuffer;
    private final Map<ItemStack, Short> transferredItems = new ItemStackMap<>();
    private final Set<ItemStack> checkedItems = new ItemStackSet();
    private final LinkedList<IInventoryObject> chests = new LinkedList<>();
    private AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> !(tile instanceof TileItemUnloader), InventorySorter.SIZE_DESCENDING);

    public TileItemUnloader() {
        setInventorySize(9);
        invBuffer = new InventoryMapper(getInventory(), false);
    }

    @Override
    public EnumRedstoneMode[] getValidRedstoneModes() {
        return REDSTONE_MODES;
    }

    @Override
    public EnumMachineGamma getMachineType() {
        return EnumMachineGamma.ITEM_UNLOADER;
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new SlotOutput(this, id, x, y);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(getWorld())) {
            return;
        }

        emptyCart();
        clearInv();
    }

    private void emptyCart() {
        movedItemCart = false;
        EntityMinecart cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos(), 0.1f, getOrientation());

        if (cart == null) {
            setPowered(false);
            currentCart = null;
            return;
        }

        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            transferredItems.clear();
            cartWasSent();
        }

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        if (isPaused())
            return;

        chests.clear();
        chests.addAll(invCache.getAdjacentInventories());
        chests.addFirst(invBuffer);

        checkedItems.clear();

        IInventoryObject cartInv = InvTools.getInventory(cart, getOrientation().getOpposite());
        if (cartInv == null) {
            sendCart(cart);
            return;
        }

        switch (getMode()) {
            case TRANSFER: {
                boolean hasFilter = false;
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    hasFilter = true;
                    Short numMoved = transferredItems.get(filter);
                    if (numMoved == null) {
                        numMoved = 0;
                    }
                    if (numMoved < InvTools.countItems(getItemFilters(), filter)) {
                        ItemStack moved = InvTools.moveOneItem(cartInv, chests, filter);
                        if (moved != null) {
                            movedItemCart = true;
                            numMoved++;
                            transferredItems.put(moved, numMoved);
                            break;
                        }
                    }
                }
                if (!hasFilter) {
                    ItemStack moved = InvTools.moveOneItem(cartInv, chests);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                break;
            }
            case STOCK: {
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    int stocked = InvTools.countItems(chests, filter);
                    if (stocked < InvTools.countItems(getItemFilters(), filter)) {
                        ItemStack moved = InvTools.moveOneItem(cartInv, chests, filter);
                        if (moved != null) {
                            movedItemCart = true;
                            break;
                        }
                    }
                }
                break;
            }
            case EXCESS: {
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    int stocked = InvTools.countItems(cartInv, filter);
                    if (stocked > InvTools.countItems(getItemFilters(), filter)) {
                        ItemStack moved = InvTools.moveOneItem(cartInv, chests, filter);
                        if (moved != null) {
                            movedItemCart = true;
                            break;
                        }
                    }
                }
                if (!movedItemCart) {
                    movedItemCart = InvTools.moveOneItemExcept(cartInv, chests, getFilters()) != null;
                }
                break;
            }
            case ALL: {
                boolean hasFilter = false;
                for (ItemStack filter : getItemFilters().getContents()) {
                    if (filter == null) {
                        continue;
                    }
                    if (!checkedItems.add(filter)) {
                        continue;
                    }
                    hasFilter = true;
                    ItemStack moved = InvTools.moveOneItem(cartInv, chests, filter);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                if (!hasFilter) {
                    ItemStack moved = InvTools.moveOneItem(cartInv, chests);
                    if (moved != null) {
                        movedItemCart = true;
                        break;
                    }
                }
                break;
            }
        }

        EnumRedstoneMode state = getRedstoneModeController().getButtonState();
        if (state != EnumRedstoneMode.MANUAL && !isPowered() && shouldSendCart(cart)) {
            sendCart(cart);
        }
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return super.canHandleCart(cart) && !InvTools.isInventoryEmpty(InventoryObject.get((IInventory) cart));
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        IInventoryObject cartInv = InvTools.getInventory(cart, getOrientation().getOpposite());
        if (cartInv == null)
            return true;
        EnumRedstoneMode state = getRedstoneModeController().getButtonState();
        if (!movedItemCart && state != EnumRedstoneMode.COMPLETE) {
            return true;
        } else if (getMode() == EnumTransferMode.TRANSFER && isTransferComplete(getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.STOCK && isStockComplete(chests, getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.EXCESS && isExcessComplete(cartInv, getItemFilters().getContents())) {
            return true;
        } else if (getMode() == EnumTransferMode.ALL && isAllComplete(chests, getItemFilters().getContents())) {
            return true;
        } else if (!movedItemCart && InvTools.isAccessibleInventoryEmpty(cartInv)) {
            return true;
        }
        return false;
    }

    private boolean isTransferComplete(ItemStack[] filters) {
        checkedItems.clear();
        boolean hasFilter = false;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            hasFilter = true;
            Short numMoved = transferredItems.get(filter);
            if (numMoved == null || numMoved < InvTools.countItems(getItemFilters(), filter)) {
                return false;
            }
        }
        return hasFilter;
    }

    private boolean isStockComplete(List<IInventoryObject> chests, ItemStack[] filters) {
        checkedItems.clear();
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            int stocked = InvTools.countItems(chests, filter);
            if (stocked < InvTools.countItems(getItemFilters(), filter)) {
                return false;
            }
        }
        return true;
    }

    private boolean isExcessComplete(IInventoryObject cartInv, ItemStack[] filters) {
        checkedItems.clear();
        int max = 0;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            int stocked = InvTools.countItems(cartInv, filter);
            max += filter.stackSize;
            if (stocked > InvTools.countItems(getItemFilters(), filter)) {
                return false;
            }
        }
        return InvTools.countItems(cartInv) <= max;
    }

    private boolean isAllComplete(List<IInventoryObject> chests, ItemStack[] filters) {
        checkedItems.clear();
        boolean hasFilter = false;
        for (ItemStack filter : filters) {
            if (filter == null) {
                continue;
            }
            if (!checkedItems.add(filter)) {
                continue;
            }
            hasFilter = true;
            if (InvTools.countItems(chests, filter) > 0) {
                return false;
            }
        }
        return hasFilter;
    }

    private void clearInv() {
        if (!InvTools.isInventoryEmpty(invBuffer)) {
            InvTools.moveOneItem(invBuffer, invCache.getAdjacentInventories());
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_ITEM, player, worldObj, getPos());
        return true;
    }

    @Override
    public EnumFacing getOrientation() {
        return EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int index, @Nullable ItemStack stack, @Nullable EnumFacing direction) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }
}
