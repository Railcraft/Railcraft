/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/
package mods.railcraft.common.util.inventory.iterators;

import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryIterator<T extends IInvSlot> implements Iterable<T> {

    public static InventoryIterator<IExtInvSlot> getIterable(IInventory inv) {
        if (inv instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv);
        return new StandardInventoryIterator(inv);
    }

    public static InventoryIterator<IInvSlot> getIterable(IItemHandler inv) {
        return new ItemHandlerInventoryIterator(inv);
    }

    public static InventoryIterator<? extends IInvSlot> getIterable(IInventoryObject inv) {
        if (inv.getInventoryObject() instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv.getInventoryObject());
        if (inv.getInventoryObject() instanceof IInventory)
            return new StandardInventoryIterator((IInventory) inv.getInventoryObject());
        if (inv.getInventoryObject() instanceof IItemHandler)
            return new ItemHandlerInventoryIterator((IItemHandler) inv.getInventoryObject());
        throw new RuntimeException("Invalid Inventory Object");
    }

    public Iterable<T> notNull() {
        List<T> filledSlots = new ArrayList<T>(32);
        for (T slot : this) {
            if (slot.getStack() != null)
                filledSlots.add(slot);
        }
        return filledSlots;
    }
}
