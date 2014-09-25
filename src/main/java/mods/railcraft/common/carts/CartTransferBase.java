/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IItemTransfer;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.util.inventory.filters.ArrayStackFilter;
import mods.railcraft.common.util.inventory.filters.StackFilter;

/**
 * Abstract minecart class that implements the IItemTransfer interface for
 * convenience and as example for others who wish to create carts that
 * implements IItemTransfer. This particular implementation assumes a simple
 * inventory and will attempt to pass along offers and requests to linked carts
 * if it cannot fulfill them itself. <br/> <br/> Classes that extend this
 * class:<br/> EntityCartChest<br/> EntityCartAnchor<br/>
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartTransferBase extends CartContainerBase implements IItemTransfer {

    /**
     * If passThrough == true, this cart will only pass requests along, it wont
     * attempt to fulfill them.
     */
    protected boolean passThrough = false;

    public CartTransferBase(World world) {
        super(world);
    }

    public CartTransferBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ItemStack offerItem(Object source, ItemStack offer) {
        if (!passThrough && getSizeInventory() > 0) {
            offer = moveItemStack(offer, this);
            if (offer == null)
                return null;
        }

        ILinkageManager lm = CartTools.getLinkageManager(worldObj);

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (linkedCart != source && linkedCart instanceof IItemTransfer)
            offer = ((IItemTransfer) linkedCart).offerItem(this, offer);

        if (offer == null)
            return null;

        linkedCart = lm.getLinkedCartB(this);
        if (linkedCart != source && linkedCart instanceof IItemTransfer)
            offer = ((IItemTransfer) linkedCart).offerItem(this, offer);

        return offer;
    }

    @Override
    public ItemStack requestItem(Object source) {
        return requestItem(this, StackFilter.ALL);
    }

    @Override
    public ItemStack requestItem(Object source, ItemStack request) {
        return requestItem(this, new ArrayStackFilter(request));
    }

    @Override
    public ItemStack requestItem(Object source, IStackFilter request) {
        ItemStack result = null;
        if (!passThrough && getSizeInventory() > 0) {
            result = removeOneItem(this, request);
            if (result != null)
                return result;
        }

        ILinkageManager lm = CartTools.getLinkageManager(worldObj);

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (linkedCart != source && linkedCart instanceof IItemTransfer)
            result = ((IItemTransfer) linkedCart).requestItem(this, request);

        if (result != null)
            return result;

        linkedCart = lm.getLinkedCartB(this);
        if (linkedCart != source && linkedCart instanceof IItemTransfer)
            result = ((IItemTransfer) linkedCart).requestItem(this, request);

        return result;
    }

    /**
     * Removes and returns a single item from the inventory that matches the
     * filter.
     *
     * @param inv The inventory
     * @param filter EnumItemType to match against
     * @return An ItemStack
     */
    private ItemStack removeOneItem(IInventory inv, IStackFilter filter) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot != null && filter.matches(slot))
                return inv.decrStackSize(i, 1);
        }
        return null;
    }

    private ItemStack moveItemStack(ItemStack stack, IInventory dest) {
        if (stack == null)
            return null;
        stack = stack.copy();
        if (dest == null)
            return stack;
        boolean movedItem = false;
        do {
            movedItem = false;
            ItemStack destStack = null;
            for (int ii = 0; ii < dest.getSizeInventory(); ii++) {
                destStack = dest.getStackInSlot(ii);
                if (destStack != null && destStack.isItemEqual(stack)) {
                    int maxStack = Math.min(destStack.getMaxStackSize(), dest.getInventoryStackLimit());
                    int room = maxStack - destStack.stackSize;
                    if (room > 0) {
                        int move = Math.min(room, stack.stackSize);
                        destStack.stackSize += move;
                        stack.stackSize -= move;
                        if (stack.stackSize <= 0)
                            return null;
                        movedItem = true;
                    }
                }
            }
            if (!movedItem)
                for (int ii = 0; ii < dest.getSizeInventory(); ii++) {
                    destStack = dest.getStackInSlot(ii);
                    if (destStack == null) {
                        if (stack.stackSize > dest.getInventoryStackLimit())
                            dest.setInventorySlotContents(ii, stack.splitStack(dest.getInventoryStackLimit()));
                        else {
                            dest.setInventorySlotContents(ii, stack);
                            return null;
                        }
                        movedItem = true;
                    }
                }
        } while (movedItem);
        return stack;
    }

}
