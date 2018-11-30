/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.iterators;

import com.google.common.collect.Streams;
import mods.railcraft.common.util.inventory.wrappers.IInventoryAdapter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryIterator<T extends IInvSlot> implements Iterable<T> {

    public static InventoryIterator<IExtInvSlot> getVanilla(IInventory inv) {
        if (inv instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv);
        return new StandardInventoryIterator(inv);
    }

    public static InventoryIterator<IInvSlot> getForge(IItemHandler inv) {
        return new ItemHandlerInventoryIterator(inv);
    }

    public static InventoryIterator<? extends IInvSlot> get(IInventoryAdapter inv) {
        Objects.requireNonNull(inv.getBackingObject());
        if (inv.getBackingObject() instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv.getBackingObject());
        if (inv.getBackingObject() instanceof IInventory)
            return new StandardInventoryIterator((IInventory) inv.getBackingObject());
        if (inv.getBackingObject() instanceof IItemHandler)
            return new ItemHandlerInventoryIterator((IItemHandler) inv.getBackingObject());
        throw new IllegalArgumentException("Invalid Inventory Object");
    }

    public Stream<T> stream() {
        return Streams.stream(this);
    }

    public Stream<ItemStack> streamStacks() {
        return stream().filter(IInvSlot::hasStack).map(IInvSlot::getStack);
    }
}
