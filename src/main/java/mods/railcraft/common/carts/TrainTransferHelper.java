/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.api.carts.ITrainTransferHelper;
import mods.railcraft.common.fluids.AdvancedFluidHandler;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.util.collections.StackKey;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class for simplifying moving items and fluids through a train.
 *
 * Created by CovertJaguar on 5/9/2015.
 */
public enum TrainTransferHelper implements ITrainTransferHelper {
    INSTANCE;
    private static final int NUM_SLOTS = 8;
    private static final int TANK_CAPACITY = 8 * FluidTools.BUCKET_VOLUME;

    /**
     * Offers an item stack to linked carts or drops it if no one wants it.
     */
    @Override
    public void offerOrDropItem(EntityMinecart cart, ItemStack stack) {
        stack = pushStack(cart, stack);

        if (!InvTools.isEmpty(stack))
            cart.entityDropItem(stack, 1);
    }

    // ***************************************************************************************************************************
    // Items
    // ***************************************************************************************************************************
    @Override
    public ItemStack pushStack(EntityMinecart requester, ItemStack stack) {
        Iterable<EntityMinecart> carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_A);
        stack = _pushStack(requester, carts, stack);
        if (InvTools.isEmpty(stack))
            return InvTools.emptyStack();
        if (LinkageManager.INSTANCE.hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_B);
            stack = _pushStack(requester, carts, stack);
        }
        return stack;
    }

    private ItemStack _pushStack(EntityMinecart requester, Iterable<EntityMinecart> carts, ItemStack stack) {
        for (EntityMinecart cart : carts) {
            InventoryComposite inv = InventoryComposite.of(cart);
            if (!inv.isEmpty() && canAcceptPushedItem(requester, cart, stack))
                stack = inv.addStack(stack);
            if (InvTools.isEmpty(stack) || blocksItemRequests(cart, stack))
                break;
        }
        return stack;
    }

    @Override
    public ItemStack pullStack(EntityMinecart requester, Predicate<ItemStack> filter) {
        Iterable<EntityMinecart> carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_A);
        ItemStack stack = _pullStack(requester, carts, filter);
        if (!InvTools.isEmpty(stack))
            return stack;
        carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_B);
        return _pullStack(requester, carts, filter);
    }

    private ItemStack _pullStack(EntityMinecart requester, Iterable<EntityMinecart> carts, Predicate<ItemStack> filter) {
        ItemStack result = ItemStack.EMPTY;
        EntityMinecart upTo = null;
        InventoryComposite targetInv = null;
        for (EntityMinecart cart : carts) {
            InventoryComposite inv = InventoryComposite.of(cart);
            if (!inv.isEmpty()) {
                Set<StackKey> items = inv.findAll(filter);
                for (StackKey stackKey : items) {
                    ItemStack stack = stackKey.get();
                    if (canProvidePulledItem(requester, cart, stack)) {
                        ItemStack toRemove = inv.findOne(StackFilters.of(stack));
                        if (!InvTools.isEmpty(toRemove)) {
                            result = toRemove;
                            upTo = cart;
                            targetInv = inv;
                            break;
                        }
                    }
                }
            }
        }

        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        for (EntityMinecart cart : carts) {
            if (cart == upTo) {
                break;
            }
            if (blocksItemRequests(cart, result)) {
                return ItemStack.EMPTY;
            }
        }

        if (targetInv != null) {
            return targetInv.removeOneItem(result);
        }

        return ItemStack.EMPTY;
    }

    private boolean canAcceptPushedItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        return !(cart instanceof IItemCart) || ((IItemCart) cart).canAcceptPushedItem(requester, stack);
    }

    private boolean canProvidePulledItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        return !(cart instanceof IItemCart) || ((IItemCart) cart).canProvidePulledItem(requester, stack);
    }

    private boolean blocksItemRequests(EntityMinecart cart, ItemStack stack) {
        if (cart instanceof IItemCart)
            return !((IItemCart) cart).canPassItemRequests(stack);
        return InventoryComposite.of(cart).slotCount() < NUM_SLOTS;
    }

    @Override
    public Optional<IItemHandlerModifiable> getTrainItemHandler(EntityMinecart cart) {
        return Train.get(cart).flatMap(Train::getItemHandler);
    }

    // ***************************************************************************************************************************
    // Fluids
    // ***************************************************************************************************************************
    @Override
    public FluidStack pushFluid(EntityMinecart requester, FluidStack fluidStack) {
        Iterable<EntityMinecart> carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_A);
        fluidStack = _pushFluid(requester, carts, fluidStack);
        if (fluidStack == null)
            return null;
        if (LinkageManager.INSTANCE.hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_B);
            fluidStack = _pushFluid(requester, carts, fluidStack);
        }
        return fluidStack;
    }

    private @Nullable FluidStack _pushFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        for (EntityMinecart cart : carts) {
            if (canAcceptPushedFluid(requester, cart, fluidStack)) {
                IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.UP, cart);
                if (fluidHandler != null)
                    fluidStack.amount -= fluidHandler.fill(fluidStack, true);
            }
            if (fluidStack.amount <= 0 || blocksFluidRequests(cart, fluidStack))
                break;
        }
        if (fluidStack.amount <= 0)
            return null;
        return fluidStack;
    }

    @Override
    public FluidStack pullFluid(EntityMinecart requester, @Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            return null;
        }
        Iterable<EntityMinecart> carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_A);
        FluidStack pulled = _pullFluid(requester, carts, fluidStack);
        if (pulled != null)
            return pulled;
        carts = LinkageManager.INSTANCE.linkIterator(requester, LinkageManager.LinkType.LINK_B);
        return _pullFluid(requester, carts, fluidStack);
    }

    private @Nullable FluidStack _pullFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        for (EntityMinecart cart : carts) {
            if (canProvidePulledFluid(requester, cart, fluidStack)) {
                IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.DOWN, cart);
                if (fluidHandler != null) {
                    FluidStack drained = fluidHandler.drain(fluidStack, true);
                    if (drained != null)
                        return drained;
                }
            }

            if (blocksFluidRequests(cart, fluidStack))
                break;
        }
        return null;
    }

    private boolean canAcceptPushedFluid(EntityMinecart requester, EntityMinecart cart, FluidStack fluid) {
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.UP, cart);
        if (fluidHandler == null)
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canAcceptPushedFluid(requester, fluid);
        AdvancedFluidHandler advancedFluidHandler = new AdvancedFluidHandler(fluidHandler);
        return advancedFluidHandler.canPutFluid(new FluidStack(fluid, 1));
    }

    private boolean canProvidePulledFluid(EntityMinecart requester, EntityMinecart cart, FluidStack fluid) {
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.DOWN, cart);
        if (fluidHandler == null)
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canProvidePulledFluid(requester, fluid);
        return !Fluids.isEmpty(fluidHandler.drain(new FluidStack(fluid, 1), false));
    }

    private boolean blocksFluidRequests(EntityMinecart cart, FluidStack fluid) {
        if (cart instanceof IFluidCart)
            return !((IFluidCart) cart).canPassFluidRequests(fluid);
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(null, cart);
        if (fluidHandler != null) {
            return !hasMatchingTank(fluidHandler, fluid);
        }
        return true;
    }

    private boolean hasMatchingTank(IFluidHandler handler, FluidStack fluid) {
        return FluidTools.testProperties(false, handler, p -> p.getCapacity() >= TANK_CAPACITY && (Fluids.isEmpty(p.getContents()) || FluidTools.matches(fluid, p.getContents())));
    }

    @Override
    public Optional<IFluidHandler> getTrainFluidHandler(EntityMinecart cart) {
        return Train.get(cart).flatMap(Train::getFluidHandler);
    }
}
