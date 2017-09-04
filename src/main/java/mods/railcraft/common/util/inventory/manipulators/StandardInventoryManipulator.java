/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.manipulators;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardInventoryManipulator extends InventoryManipulator<IExtInvSlot> {

    private final IInventory inv;

    protected StandardInventoryManipulator(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public Iterator<IExtInvSlot> iterator() {
        return InventoryIterator.getVanilla(inv).iterator();
    }

    @Override
    protected ItemStack addStack(ItemStack stack, boolean doAdd) {
        if (InvTools.isEmpty(stack))
            return InvTools.emptyStack();
        stack = stack.copy();
        List<IExtInvSlot> filledSlots = new ArrayList<IExtInvSlot>(inv.getSizeInventory());
        List<IExtInvSlot> emptySlots = new ArrayList<IExtInvSlot>(inv.getSizeInventory());
        for (IExtInvSlot slot : this) {
            if (slot.canPutStackInSlot(stack))
                if (InvTools.isEmpty(slot.getStack()))
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
        }

        int injected = 0;
        injected = tryPut(filledSlots, stack, injected, doAdd);
        injected = tryPut(emptySlots, stack, injected, doAdd);
        decSize(stack, injected);
        if (isEmpty(stack))
            return InvTools.emptyStack();
        return stack;
    }

    private int tryPut(List<IExtInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
        if (injected >= sizeOf(stack))
            return injected;
        for (IExtInvSlot slot : slots) {
            ItemStack stackInSlot = slot.getStack();
            if (InvTools.isEmpty(stackInSlot) || InvTools.isItemEqual(stackInSlot, stack)) {
                int used = addToSlot(slot, stack, sizeOf(stack) - injected, doAdd);
                if (used > 0) {
                    injected += used;
                    if (injected >= sizeOf(stack))
                        return injected;
                }
            }
        }
        return injected;
    }

    /**
     * @param available Amount we can move
     * @return Return the number of items moved.
     */
    private int addToSlot(IExtInvSlot slot, ItemStack stack, int available, boolean doAdd) {
        int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());

        ItemStack stackInSlot = slot.getStack();
        if (InvTools.isEmpty(stackInSlot)) {
            int wanted = Math.min(available, max);
            if (doAdd) {
                stackInSlot = stack.copy();
                setSize(stackInSlot, wanted);
                slot.setStack(stackInSlot);
            }
            return wanted;
        }

        if (!InvTools.isItemEqual(stack, stackInSlot))
            return 0;

        int wanted = max - sizeOf(stackInSlot);
        if (wanted <= 0)
            return 0;

        if (wanted > available)
            wanted = available;

        if (doAdd) {
            incSize(stackInSlot, wanted);
            slot.setStack(stackInSlot);
        }
        return wanted;
    }

    @Override
    @Nonnull
    protected List<ItemStack> removeItem(Predicate<ItemStack> filter, int maxAmount, boolean doRemove) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<ItemStack>();
        for (IExtInvSlot slot : this) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
                ItemStack output = stack.copy();
                if (sizeOf(output) >= amountNeeded) {
                    setSize(output, amountNeeded);
                    if (doRemove) {
                        decSize(stack, amountNeeded);
                        if (isEmpty(stack))
                            stack = InvTools.emptyStack();
                        slot.setStack(stack);
                    }
                    amountNeeded = 0;
                    outputList.add(output);
                } else {
                    amountNeeded -= sizeOf(output);
                    outputList.add(output);
                    if (doRemove)
                        slot.setStack(InvTools.emptyStack());
                }
            }
        }
        return outputList;
    }

}
