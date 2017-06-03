/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.iterators;

import com.google.common.collect.Iterators;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public static InventoryIterator<? extends IInvSlot> getRailcraft(IInventoryObject inv) {
        if (inv.getBackingObject() instanceof ISidedInventory)
            return new SidedInventoryIterator((ISidedInventory) inv.getBackingObject());
        if (inv.getBackingObject() instanceof IInventory)
            return new StandardInventoryIterator((IInventory) inv.getBackingObject());
        if (inv.getBackingObject() instanceof IItemHandler)
            return new ItemHandlerInventoryIterator((IItemHandler) inv.getBackingObject());
        throw new RuntimeException("Invalid Inventory Object");
    }

    public Iterable<ItemStack> getStacks() {
        return () -> Iterators.transform(Iterators.filter(iterator(), s -> s != null && s.hasStack()), s -> s != null ? s.getStack() : null);
    }

    public Stream<ItemStack> getStackStream() {
        return StreamSupport.stream(getStacks().spliterator(), false);
    }
}
