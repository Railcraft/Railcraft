/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 * Created by CovertJaguar on 4/2/2015.
 */
public class FluidItemHelper {
    /**
     * Fill a liquid container.
     *
     * @return The modified container and the amount of Fluid filled.
     */
    public static FillReturn fillContainer(ItemStack container, FluidStack fluidStack) {
        if (container == null)
            return new FillReturn(null, 0);
        container = container.copy();
        if (fluidStack == null)
            return new FillReturn(container, 0);
        if (container.getItem() instanceof IFluidContainerItem) {
            container.stackSize = 1;
            IFluidContainerItem fluidCon = (IFluidContainerItem) container.getItem();
            return new FillReturn(container, fluidCon.fill(container, fluidStack, true));
        }
        ItemStack filledCon = FluidContainerRegistry.fillFluidContainer(fluidStack, container);
        if (filledCon != null)
            return new FillReturn(filledCon, FluidContainerRegistry.getFluidForFilledItem(filledCon).amount);
        return new FillReturn(container, 0);
    }

    public static FillReturn fillContainer(ItemStack stackToFill, Fluid fluid) {
        return fillContainer(stackToFill, new FluidStack(fluid, Integer.MAX_VALUE));
    }

    /**
     * Drain a liquid container.
     *
     * @return The modified container and any fluid drained.
     */
    public static DrainReturn drainContainer(ItemStack container, int maxDrain) {
        if (container == null)
            return new DrainReturn(null, null, false);
        container = container.copy();
        if (container.getItem() instanceof IFluidContainerItem) {
            Item item = container.getItem();
            container.stackSize = 1;
            IFluidContainerItem fluidCon = (IFluidContainerItem) item;
            FluidStack drained = fluidCon.drain(container, maxDrain, true);
            ItemStack returnStack;
            if (container.getItem().hasContainerItem(container)) {
                returnStack = container.getItem().getContainerItem(container);
            } else {
                returnStack = container;
            }
            return new DrainReturn(returnStack, drained, false);
        }
        if (FluidContainerRegistry.isFilledContainer(container)) {
            ItemStack emptyCon = container.getItem().getContainerItem(container);
            return new DrainReturn(emptyCon, FluidContainerRegistry.getFluidForFilledItem(container), true);
        }
        return new DrainReturn(container, null, false);
    }

    public static boolean isBucket(ItemStack stack) {
        return FluidContainerRegistry.isBucket(stack);
    }

    public static boolean isContainer(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            return ((IFluidContainerItem) stack.getItem()).getCapacity(stack) > 0;
        }
        return FluidContainerRegistry.isContainer(stack);
    }

    public static boolean isFilledContainer(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            return fluidCon.getFluid(stack) != null && fluidCon.getFluid(stack).amount > 0;
        }
        return FluidContainerRegistry.isFilledContainer(stack);
    }

    public static boolean isFullContainer(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            int capacity = fluidCon.getCapacity(stack);
            FluidStack fluidStack = fluidCon.getFluid(stack);
            return capacity > 0 && fluidStack != null && fluidStack.amount >= capacity;
        }
        return FluidContainerRegistry.isFilledContainer(stack);
    }

    public static boolean isEmptyContainer(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            FluidStack fluidStack = fluidCon.getFluid(stack);
            return fluidCon.getCapacity(stack) > 0 && (fluidStack == null || fluidStack.amount <= 0);
        }
        return FluidContainerRegistry.isEmptyContainer(stack);
    }

    public static boolean isRoomInContainer(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            FluidStack fluidStack = fluidCon.getFluid(stack);
            int capacity = fluidCon.getCapacity(stack);
            return capacity > 0 && (fluidStack == null || fluidStack.amount < capacity);
        }
        return FluidContainerRegistry.isEmptyContainer(stack);
    }

    public static boolean isRoomInContainer(ItemStack stack, Fluid fluid) {
        if (stack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            return fluidCon.fill(stack, new FluidStack(fluid, 1), false) > 0;
        }
        return FluidContainerRegistry.isEmptyContainer(stack);
    }

    public static int getRoomInContainer(ItemStack stack, Fluid fluid) {
        if (stack == null) return 0;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            return fluidCon.fill(stack, new FluidStack(fluid, Integer.MAX_VALUE), false);
        }
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, Integer.MAX_VALUE), stack);
        if (filled != null) {
            FluidStack filledFluid = getFluidStackInContainer(filled);
            return filledFluid != null ? filledFluid.amount : 0;
        }
        return 0;
    }

    public static boolean containsFluid(ItemStack stack, Fluid fluid) {
        if (stack == null || fluid == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            return Fluids.areEqual(fluid, fluidCon.getFluid(stack));
        }
        return FluidContainerRegistry.containsFluid(stack, new FluidStack(fluid, 1));
    }

    public static boolean containsFluid(ItemStack stack, FluidStack fluidStack) {
        if (stack == null || fluidStack == null) return false;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            FluidStack conFluidStack = fluidCon.getFluid(stack);
            return conFluidStack != null && conFluidStack.containsFluid(fluidStack);
        }
        return FluidContainerRegistry.containsFluid(stack, fluidStack);
    }

    public static FluidStack getFluidStackInContainer(ItemStack stack) {
        if (stack == null) return null;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            return fluidCon.getFluid(stack);
        }
        return FluidContainerRegistry.getFluidForFilledItem(stack);
    }

    public static Fluid getFluidInContainer(ItemStack stack) {
        if (stack == null) return null;
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidCon = (IFluidContainerItem) stack.getItem();
            FluidStack conFluidStack = fluidCon.getFluid(stack);
            return conFluidStack == null ? null : conFluidStack.getFluid();
        }
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
        return fluidStack != null ? fluidStack.getFluid() : null;
    }

    public static class FillReturn {
        public final ItemStack container;
        public final int amount;

        public FillReturn(ItemStack con, int amount) {
            this.container = con;
            this.amount = amount;
        }
    }

    public static class DrainReturn {
        public final ItemStack container;
        public final FluidStack fluidDrained;
        public final boolean isAtomic;

        public DrainReturn(ItemStack con, FluidStack fluidDrained, boolean isAtomic) {
            this.container = con;
            this.fluidDrained = fluidDrained;
            this.isAtomic = isAtomic;
        }
    }
}
