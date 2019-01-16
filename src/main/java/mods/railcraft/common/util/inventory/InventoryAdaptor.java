/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.inventory.wrappers.ChestWrapper;
import mods.railcraft.common.util.inventory.wrappers.SidedInventoryDecorator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * Represents a single Inventory object.
 *
 * It operates as an adaptor class to provide a single wrapper interface for a variety
 * if inventory APIs.
 *
 * Supported inventory API objects include:
 * {@link IInventory}, {@link ISidedInventory}, and {@link IItemHandler}
 *
 * Most manipulation functions are implemented through an
 * {@link InventoryIterator} where the actual polymorphism happens.
 *
 * This particular framework is designed to operation on slot based APIs.
 * If an API is not slot based, it will probably be difficult to adapt this framework to it.
 *
 * Created by CovertJaguar on 3/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryAdaptor implements IInventoryManipulator {
    private final Object inventory;

    private InventoryAdaptor(Object inventory) {
        this.inventory = inventory;
    }

    public Object getBackingObject() {
        return inventory;
    }

    static InventoryAdaptor of(final IInventory inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int slotCount() {
                return inventory.getSizeInventory();
            }
        };
    }

    static InventoryAdaptor of(final IItemHandler inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int slotCount() {
                return inventory.getSlots();
            }
        };
    }

    static Optional<InventoryAdaptor> of(@Nullable Object obj) {
        return of(obj, null);
    }

    static Optional<InventoryAdaptor> of(@Nullable Object obj, @Nullable EnumFacing side) {
        InventoryAdaptor inv = null;
        if (obj instanceof InventoryAdaptor) {
            inv = (InventoryAdaptor) obj;
        } else if (obj instanceof ICapabilityProvider && ((ICapabilityProvider) obj).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            inv = of(
                    Objects.requireNonNull(
                            ((ICapabilityProvider) obj).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                    )
            );
        } else if (obj instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) obj;
            inv = of(new ChestWrapper(chest));
        } else if (side != null && obj instanceof ISidedInventory) {
            inv = of(new SidedInventoryDecorator((ISidedInventory) obj, side));
        } else if (obj instanceof IInventory) {
            inv = of((IInventory) obj);
        }
        return Optional.ofNullable(inv);
    }

    @Override
    public boolean canFit(ItemStack stack) {
        return isEmpty(addStack(stack, InvOp.SIMULATE));
    }

    /**
     * Attempt to add the stack to the inventory returning the remainder.
     *
     * If the entire stack was accepted, it returns an empty stack.
     *
     * @return The remainder
     */
    @Override
    public ItemStack addStack(ItemStack stack, InvOp op) {
        if (isEmpty(stack))
            return emptyStack();
        stack = stack.copy();
        List<IInvSlot> filledSlots = new ArrayList<>();
        List<IInvSlot> emptySlots = new ArrayList<>();
        for (IInvSlot slot : InventoryIterator.get(this)) {
            if (slot.canPutStackInSlot(stack)) {
                if (isEmpty(slot.getStack()))
                    emptySlots.add(slot);
                else
                    filledSlots.add(slot);
            }
        }

        int injected = 0;
        injected = InvTools.tryPut(filledSlots, stack, injected, op);
        injected = InvTools.tryPut(emptySlots, stack, injected, op);
        decSize(stack, injected);
        if (isEmpty(stack))
            return emptyStack();
        return stack;
    }

    /**
     * Removes up to maxAmount items in one slot matching the filter.
     */
    @Override
    public ItemStack removeStack(int maxAmount, Predicate<ItemStack> filter, InvOp op) {
        for (IInvSlot slot : InventoryIterator.get(this)) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack) && slot.canTakeStackFromSlot() && filter.test(stack)) {
                return slot.removeFromSlot(maxAmount, op);
            }
        }
        return emptyStack();
    }

    @Override
    public List<ItemStack> extractItems(int maxAmount, Predicate<ItemStack> filter, InvOp op) {
        int amountNeeded = maxAmount;
        List<ItemStack> outputList = new ArrayList<>();
        for (IInvSlot slot : InventoryIterator.get(this)) {
            if (amountNeeded <= 0)
                break;
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && slot.canTakeStackFromSlot() && filter.test(stack)) {
                ItemStack output = slot.removeFromSlot(amountNeeded, op);
                if (!isEmpty(output)) {
                    amountNeeded -= sizeOf(output);
                    outputList.add(output);
                }
            }
        }
        return outputList;
    }

    @Override
    public ItemStack moveOneItemTo(IInventoryComposite dest, Predicate<ItemStack> filter) {
        for (IInvSlot slot : InventoryIterator.get(this)) {
            if (slot.hasStack() && slot.canTakeStackFromSlot() && slot.matches(filter)) {
                ItemStack stack = slot.getStack();
                stack = InvTools.copyOne(stack);
                stack = dest.addStack(stack);
                if (isEmpty(stack))
                    return slot.decreaseStack();
            }
        }
        return emptyStack();
    }

    @Override
    public Stream<? extends IInvSlot> streamSlots() {
        return InventoryIterator.get(this).stream();
    }

    @Override
    public Stream<ItemStack> streamStacks() {
        return InventoryIterator.get(this).streamStacks();
    }
}
