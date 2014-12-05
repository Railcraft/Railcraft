/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.tileentity.TileEntity;

public class TileItemUnloader extends TileLoaderItemBase {

    private static final EnumRedstoneMode[] REDSTONE_MODES = new EnumRedstoneMode[]{EnumRedstoneMode.IMMEDIATE, EnumRedstoneMode.COMPLETE, EnumRedstoneMode.MANUAL};
    private final IInventory invBuffer;
    private final Map<ItemStack, Short> transferedItems = new ItemStackMap<Short>();
    private final Set<ItemStack> checkedItems = new ItemStackSet();
    private AdjacentInventoryCache invCache = new AdjacentInventoryCache(this, tileCache, new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileItemUnloader) {
                return false;
            }
            return true;
        }

    }, InventorySorter.SIZE_DECENDING);

    public TileItemUnloader() {
        super();
        setInventorySize(9);
        invBuffer = new InventoryMapper(getInventory(), false);
    }

    @Override
    public EnumRedstoneMode[] getValidRedstoneModes() {
        return REDSTONE_MODES;
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.ITEM_UNLOADER;
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new SlotOutput(this, id, x, y);
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld())) {
            return;
        }

        emptyCart();
        clearInv();
    }

    private void emptyCart() {
        movedItemCart = false;
        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0.1f, getOrientation());

        if (cart == null) {
            setPowered(false);
            currentCart = null;
            return;
        }

        IInventory cartInv = null;
        if (cart instanceof IInventory) {
            cartInv = (IInventory) cart;
        }

        if (cartInv == null || cartInv.getSizeInventory() <= 0 || InvTools.isInventoryEmpty(cartInv)) {
            if (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY) || cart.isPoweredCart()) {
                setPowered(true);
            }
            return;
        }


        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            transferedItems.clear();
            cartWasSent();
        }

        if (isSendCartGateAction()) {
            if (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY)) {
                setPowered(true);
            }
            return;
        }

        ItemStack minecartSlot1 = getCartFilters().getStackInSlot(0);
        ItemStack minecartSlot2 = getCartFilters().getStackInSlot(1);
        if (minecartSlot1 != null || minecartSlot2 != null) {
            if (!CartUtils.doesCartMatchFilter(minecartSlot1, cart) && !CartUtils.doesCartMatchFilter(minecartSlot2, cart)) {
                if (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY)) {
                    setPowered(true);
                }
                return;
            }
        }

        if (isPaused())
            return;

        LinkedList<IInventory> chests = new LinkedList<IInventory>(invCache.getAdjecentInventories());
        chests.addLast(invBuffer);

        checkedItems.clear();

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
                    Short numMoved = transferedItems.get(filter);
                    if (numMoved == null) {
                        numMoved = 0;
                    }
                    if (numMoved < InvTools.countItems(getItemFilters(), filter)) {
                        ItemStack moved = InvTools.moveOneItem(cartInv, chests, filter);
                        if (moved != null) {
                            movedItemCart = true;
                            numMoved++;
                            transferedItems.put(moved, numMoved);
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
                    movedItemCart = InvTools.moveOneItemExcept(cartInv, chests, getItemFilters().getContents()) != null;
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
        if (state != EnumRedstoneMode.MANUAL && !isPowered() && (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY) || cart.isPoweredCart())) {
            if (!movedItemCart && state != EnumRedstoneMode.COMPLETE) {
                setPowered(true);
            } else if (getMode() == EnumTransferMode.TRANSFER && isTranferComplete(getItemFilters().getContents())) {
                setPowered(true);
            } else if (getMode() == EnumTransferMode.STOCK && isStockComplete(chests, getItemFilters().getContents())) {
                setPowered(true);
            } else if (getMode() == EnumTransferMode.EXCESS && isExcessComplete(cartInv, getItemFilters().getContents())) {
                setPowered(true);
            } else if (getMode() == EnumTransferMode.ALL && isAllComplete(chests, getItemFilters().getContents())) {
                setPowered(true);
            } else if (!movedItemCart && InvTools.isAccessibleInventoryEmpty(cartInv, getOrientation().getOpposite())) {
                setPowered(true);
            }
        }
    }

    private boolean isTranferComplete(ItemStack[] filters) {
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
            Short numMoved = transferedItems.get(filter);
            if (numMoved == null || numMoved < InvTools.countItems(getItemFilters(), filter)) {
                return false;
            }
        }
        return hasFilter;
    }

    private boolean isStockComplete(List<IInventory> chests, ItemStack[] filters) {
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

    private boolean isExcessComplete(IInventory cartInv, ItemStack[] filters) {
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
        if (InvTools.countItems(cartInv) > max) {
            return false;
        }
        return true;
    }

    private boolean isAllComplete(List<IInventory> chests, ItemStack[] filters) {
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
            InvTools.moveOneItem(invBuffer, invCache.getAdjecentInventories());
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_ITEM, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    public ForgeDirection getOrientation() {
        return ForgeDirection.UP;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }

}
