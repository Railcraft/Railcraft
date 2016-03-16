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

import mods.railcraft.common.util.inventory.InventoryObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryIterator<T extends IInvSlot> implements Iterable<T> {

    public static StandardInventoryIterator getIterable(IInventory inv) {
        if (inv instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv);
        return new StandardInventoryIterator(inv);
    }

    public static ItemHandlerInventoryIterator getIterable(IItemHandler inv) {
        return new ItemHandlerInventoryIterator(inv);
    }

    public static InventoryIterator getIterable(InventoryObject inv) {
        if (inv.getObject() instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv.getObject());
        if (inv.getObject() instanceof IInventory)
            return new StandardInventoryIterator((IInventory) inv.getObject());
        if (inv.getObject() instanceof IItemHandler)
            return new ItemHandlerInventoryIterator((IItemHandler) inv.getObject());
        throw new RuntimeException("Invalid Inventory Object");
    }

    public Iterable<T> notNull() {
        List<T> filledSlots = new ArrayList<T>(32);
        for (T slot : this) {
            if (slot.getStackInSlot() != null)
                filledSlots.add(slot);
        }
        return filledSlots;
    }
}
