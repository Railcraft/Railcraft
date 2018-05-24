/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.common.util.inventory.InventoryFactory;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class for simplifying moving items and fluids through a train.
 *
 * Created by CovertJaguar on 5/9/2015.
 */
public class TrainTransferHelper implements mods.railcraft.api.carts.ITrainTransferHelper {
    public static final ITrainTransferHelper INSTANCE = new TrainTransferHelper();
    private static final int NUM_SLOTS = 8;
    private static final int TANK_CAPACITY = 8 * FluidTools.BUCKET_VOLUME;

    private TrainTransferHelper() {
    }

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
        Iterable<EntityMinecart> carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_A);
        stack = _pushStack(requester, carts, stack);
        if (InvTools.isEmpty(stack))
            return InvTools.emptyStack();
        if (LinkageManager.instance().hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_B);
            stack = _pushStack(requester, carts, stack);
        }
        return stack;
    }

    @Nullable
    private ItemStack _pushStack(EntityMinecart requester, Iterable<EntityMinecart> carts, ItemStack stack) {
        for (EntityMinecart cart : carts) {
            InventoryComposite inv = InventoryComposite.of(cart);
            if (!inv.isEmpty() && canAcceptPushedItem(requester, cart, stack))
                stack = InvTools.moveItemStack(stack, inv);
            if (InvTools.isEmpty(stack) || !canPassItemRequests(cart))
                break;
        }
        return stack;
    }

    @Override
    public ItemStack pullStack(EntityMinecart requester, Predicate<ItemStack> filter) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_A);
        ItemStack stack = _pullStack(requester, carts, filter);
        if (!InvTools.isEmpty(stack))
            return stack;
        carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_B);
        return _pullStack(requester, carts, filter);
    }

    private ItemStack _pullStack(EntityMinecart requester, Iterable<EntityMinecart> carts, Predicate<ItemStack> filter) {
        for (EntityMinecart cart : carts) {
            InventoryComposite inv = InventoryComposite.of(cart);
            if (!inv.isEmpty()) {
                Set<StackKey> items = InvTools.findMatchingItems(inv, filter);
                for (StackKey stackKey : items) {
                    ItemStack stack = stackKey.get();
                    if (canProvidePulledItem(requester, cart, stack)) {
                        ItemStack removed = InvTools.removeOneItem(inv, stack);
                        if (!InvTools.isEmpty(removed))
                            return removed;
                    }
                }
            }
            if (!canPassItemRequests(cart))
                break;
        }
        return InvTools.emptyStack();
    }

    private boolean canAcceptPushedItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        return !(cart instanceof IItemCart) || ((IItemCart) cart).canAcceptPushedItem(requester, stack);
    }

    private boolean canProvidePulledItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        return !(cart instanceof IItemCart) || ((IItemCart) cart).canProvidePulledItem(requester, stack);
    }

    private boolean canPassItemRequests(EntityMinecart cart) {
        if (cart instanceof IItemCart)
            return ((IItemCart) cart).canPassItemRequests();
        IInventoryObject inv = InventoryFactory.get(cart);
        return inv != null && inv.getNumSlots() >= NUM_SLOTS;
    }

    @Nullable
    @Override
    public IItemHandler getTrainItemHandler(EntityMinecart cart) {
        Train train = Train.getTrain(cart);
        return train.getItemHandler();
    }

    // ***************************************************************************************************************************
    // Fluids
    // ***************************************************************************************************************************
    @Override
    public FluidStack pushFluid(EntityMinecart requester, FluidStack fluidStack) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_A);
        fluidStack = _pushFluid(requester, carts, fluidStack);
        if (fluidStack == null)
            return null;
        if (LinkageManager.instance().hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_B);
            fluidStack = _pushFluid(requester, carts, fluidStack);
        }
        return fluidStack;
    }

    @Nullable
    private FluidStack _pushFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        for (EntityMinecart cart : carts) {
            if (canAcceptPushedFluid(requester, cart, fluidStack.getFluid())) {
                IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.UP, cart);
                if (fluidHandler != null)
                    fluidStack.amount -= fluidHandler.fill(fluidStack, true);
            }
            if (fluidStack.amount <= 0 || !canPassFluidRequests(cart, fluidStack.getFluid()))
                break;
        }
        if (fluidStack.amount <= 0)
            return null;
        return fluidStack;
    }

    @Override
    public FluidStack pullFluid(EntityMinecart requester, FluidStack fluidStack) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_A);
        FluidStack pulled = _pullFluid(requester, carts, fluidStack);
        if (pulled != null)
            return pulled;
        carts = LinkageManager.instance().linkIterator(requester, LinkageManager.LinkType.LINK_B);
        return _pullFluid(requester, carts, fluidStack);
    }

    @Nullable
    private FluidStack _pullFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        for (EntityMinecart cart : carts) {
            if (canProvidePulledFluid(requester, cart, fluidStack.getFluid())) {
                IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.DOWN, cart);
                if (fluidHandler != null) {
                    FluidStack drained = fluidHandler.drain(fluidStack, true);
                    if (drained != null)
                        return drained;
                }
            }

            if (!canPassFluidRequests(cart, fluidStack.getFluid()))
                break;
        }
        return null;
    }

    private boolean canAcceptPushedFluid(EntityMinecart requester, EntityMinecart cart, Fluid fluid) {
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.UP, cart);
        if (fluidHandler == null)
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canAcceptPushedFluid(requester, fluid);
        AdvancedFluidHandler advancedFluidHandler = new AdvancedFluidHandler(fluidHandler);
        return advancedFluidHandler.canPutFluid(new FluidStack(fluid, 1));
    }

    private boolean canProvidePulledFluid(EntityMinecart requester, EntityMinecart cart, Fluid fluid) {
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(EnumFacing.DOWN, cart);
        if (fluidHandler == null)
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canProvidePulledFluid(requester, fluid);
        return !Fluids.isEmpty(fluidHandler.drain(new FluidStack(fluid, 1), false));
    }

    private boolean canPassFluidRequests(EntityMinecart cart, Fluid fluid) {
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canPassFluidRequests(fluid);
        IFluidHandler fluidHandler = FluidTools.getFluidHandler(null, cart);
        if (fluidHandler != null) {
            if (hasMatchingTank(fluidHandler, fluid))
                return true;
        }
        return false;
    }

    private boolean hasMatchingTank(IFluidHandler handler, Fluid fluid) {
        return FluidTools.testProperties(false, handler, p -> p.getCapacity() >= TANK_CAPACITY && (Fluids.isEmpty(p.getContents()) || Fluids.areEqual(fluid, p.getContents())));
    }

    @Nullable
    @Override
    public IFluidHandler getTrainFluidHandler(EntityMinecart cart) {
        Train train = Train.getTrain(cart);
        return train.getFluidHandler();
    }
}
