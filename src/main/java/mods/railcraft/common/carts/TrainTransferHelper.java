/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.api.carts.ITrainTransferHelper;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Set;

/**
 * Created by CovertJaguar on 5/9/2015.
 */
public class TrainTransferHelper implements mods.railcraft.api.carts.ITrainTransferHelper {
    public static final ITrainTransferHelper INSTANCE = new TrainTransferHelper();
    private static final int NUM_SLOTS = 8;
    private static final int TANK_CAPACITY = 8 * FluidHelper.BUCKET_VOLUME;

    private TrainTransferHelper() {
    }

    // ***************************************************************************************************************************
    // Items
    // ***************************************************************************************************************************
    @Override
    public ItemStack pushStack(EntityMinecart requester, ItemStack stack) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_A);
        stack = _pushStack(requester, carts, stack);
        if (stack == null)
            return null;
        if (LinkageManager.instance().hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_B);
            stack = _pushStack(requester, carts, stack);
        }
        return stack;
    }

    private ItemStack _pushStack(EntityMinecart requester, Iterable<EntityMinecart> carts, ItemStack stack) {
        for (EntityMinecart cart : carts) {
            if (canAcceptPushedItem(requester, cart, stack))
                stack = InvTools.moveItemStack(stack, (IInventory) cart);
            if (stack == null || !canPassItemRequests(cart))
                break;
        }
        return stack;
    }

    @Override
    public ItemStack pullStack(EntityMinecart requester, IStackFilter filter) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_A);
        ItemStack stack = _pullStack(requester, carts, filter);
        if (stack != null)
            return stack;
        carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_B);
        return _pullStack(requester, carts, filter);
    }

    private ItemStack _pullStack(EntityMinecart requester, Iterable<EntityMinecart> carts, IStackFilter filter) {
        for (EntityMinecart cart : carts) {
            if (cart instanceof IInventory) {
                Set<ItemStack> items = InvTools.findMatchingItems((IInventory) cart, filter);
                for (ItemStack stack : items) {
                    if (stack != null && canProvidePulledItem(requester, cart, stack)) {
                        ItemStack removed = InvTools.removeOneItem((IInventory) cart, stack);
                        if (removed != null)
                            return removed;
                    }
                }
            }
            if (!canPassItemRequests(cart))
                break;
        }
        return null;
    }

    private boolean canAcceptPushedItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        if (!(cart instanceof IInventory))
            return false;
        if (cart instanceof IItemCart)
            return ((IItemCart) cart).canAcceptPushedItem(requester, stack);
        return true;
    }

    private boolean canProvidePulledItem(EntityMinecart requester, EntityMinecart cart, ItemStack stack) {
        if (!(cart instanceof IInventory))
            return false;
        if (cart instanceof IItemCart)
            return ((IItemCart) cart).canProvidePulledItem(requester, stack);
        return true;
    }

    private boolean canPassItemRequests(EntityMinecart cart) {
        if (cart instanceof IItemCart)
            return ((IItemCart) cart).canPassItemRequests();
        if (cart instanceof IInventory)
            return ((IInventory) cart).getSizeInventory() >= NUM_SLOTS;
        return false;
    }

    // ***************************************************************************************************************************
    // Fluids
    // ***************************************************************************************************************************
    @Override
    public FluidStack pushFluid(EntityMinecart requester, FluidStack fluidStack) {
        Iterable<EntityMinecart> carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_A);
        fluidStack = _pushFluid(requester, carts, fluidStack);
        if (fluidStack == null)
            return null;
        if (LinkageManager.instance().hasLink(requester, LinkageManager.LinkType.LINK_B)) {
            carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_B);
            fluidStack = _pushFluid(requester, carts, fluidStack);
        }
        return fluidStack;
    }

    private FluidStack _pushFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        if (fluidStack == null)
            return null;
        for (EntityMinecart cart : carts) {
            if (canAcceptPushedFluid(requester, cart, fluidStack.getFluid())) {
                fluidStack.amount -= ((IFluidHandler) cart).fill(ForgeDirection.UP, fluidStack, true);
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
        Iterable<EntityMinecart> carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_A);
        FluidStack pulled = _pullFluid(requester, carts, fluidStack);
        if (pulled != null)
            return pulled;
        carts = LinkageManager.instance().getLinkedCarts(requester, LinkageManager.LinkType.LINK_B);
        return _pullFluid(requester, carts, fluidStack);
    }

    private FluidStack _pullFluid(EntityMinecart requester, Iterable<EntityMinecart> carts, FluidStack fluidStack) {
        for (EntityMinecart cart : carts) {
            if (canProvidePulledFluid(requester, cart, fluidStack.getFluid())) {
                IFluidHandler fluidHandler = (IFluidHandler) cart;
                if (fluidHandler.canDrain(ForgeDirection.DOWN, fluidStack.getFluid())) {
                    FluidStack drained = fluidHandler.drain(ForgeDirection.DOWN, fluidStack, true);
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
        if (!(cart instanceof IFluidHandler))
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canAcceptPushedFluid(requester, fluid);
        return ((IFluidHandler) cart).canFill(ForgeDirection.UP, fluid);
    }

    private boolean canProvidePulledFluid(EntityMinecart requester, EntityMinecart cart, Fluid fluid) {
        if (!(cart instanceof IFluidHandler))
            return false;
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canProvidePulledFluid(requester, fluid);
        return ((IFluidHandler) cart).canDrain(ForgeDirection.DOWN, fluid);
    }

    private boolean canPassFluidRequests(EntityMinecart cart, Fluid fluid) {
        if (cart instanceof IFluidCart)
            return ((IFluidCart) cart).canPassFluidRequests(fluid);
        if (cart instanceof IFluidHandler) {
            if (hasMatchingTank((IFluidHandler) cart, fluid))
                return true;
        }
        return false;
    }

    private boolean hasMatchingTank(IFluidHandler handler, Fluid fluid) {
        FluidTankInfo[] tankInfo = handler.getTankInfo(ForgeDirection.UP);
        for (FluidTankInfo info : tankInfo) {
            if (info.capacity >= TANK_CAPACITY) {
                if (info.fluid == null || info.fluid.amount == 0 || info.fluid.getFluid() == fluid)
                    return true;
            }
        }
        return false;
    }
}
