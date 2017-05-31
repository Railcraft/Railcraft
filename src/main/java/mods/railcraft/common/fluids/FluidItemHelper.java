/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Helper functions for Fluid Items
 *
 * Created by CovertJaguar on 4/2/2015.
 */
public class FluidItemHelper {
    /**
     * Fill a liquid container.
     *
     * @return The modified container and the amount of Fluid filled.
     */
    public static FillReturn fillContainer(@Nullable ItemStack container, @Nullable FluidStack fluidStack) {
        if (InvTools.isEmpty(container))
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
        if (!InvTools.isEmpty(filledCon))
            return new FillReturn(filledCon, FluidContainerRegistry.getFluidForFilledItem(filledCon).amount);
        return new FillReturn(container, 0);
    }

    public static FillReturn fillContainer(@Nullable ItemStack stackToFill, Fluid fluid) {
        return fillContainer(stackToFill, new FluidStack(fluid, Integer.MAX_VALUE));
    }

    /**
     * Drain a liquid container.
     *
     * @return The modified container and any fluid drained.
     */
    public static DrainReturn drainContainer(@Nullable ItemStack container, int maxDrain) {
        if (InvTools.isEmpty(container))
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

    public static boolean isBucket(@Nullable ItemStack stack) {
        return FluidContainerRegistry.isBucket(stack);
    }

    public static boolean isContainer(@Nullable ItemStack stack) {
        return FluidUtil.getFluidHandler(stack) != null;
    }

    public static boolean testContainerProperties(boolean all, @Nullable ItemStack stack, Predicate<IFluidTankProperties> test) {
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
        return FluidTools.testProperties(all, fluidHandler, test);
    }

    public static boolean isFluidInContainer(@Nullable ItemStack stack) {
        return testContainerProperties(false, stack, p -> {
            FluidStack contents = p.getContents();
            return contents != null && contents.amount > 0;
        });
    }

    public static boolean isFullContainer(@Nullable ItemStack stack) {
        return testContainerProperties(true, stack, p -> {
            FluidStack contents = p.getContents();
            return contents != null && contents.amount >= p.getCapacity();
        });
    }

    public static boolean isEmptyContainer(@Nullable ItemStack stack) {
        return testContainerProperties(true, stack, p -> {
            FluidStack contents = p.getContents();
            return Fluids.isEmpty(contents);
        });
    }

    public static boolean isRoomInContainer(@Nullable ItemStack stack) {
        return testContainerProperties(false, stack, p -> {
            FluidStack contents = p.getContents();
            return contents == null || contents.amount < p.getCapacity();
        });
    }

    public static boolean isRoomInContainer(@Nullable ItemStack stack, @Nullable Fluid fluid) {
        if (fluid == null) return false;
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
        return fluidHandler != null && fluidHandler.fill(new FluidStack(fluid, 1), false) > 0;
    }

    public static boolean containsFluid(@Nullable ItemStack stack, @Nullable Fluid fluid) {
        return testContainerProperties(false, stack, p -> Fluids.areEqual(fluid, p.getContents()));
    }

    public static boolean containsFluid(@Nullable ItemStack stack, @Nullable FluidStack fluidStack) {
        return testContainerProperties(false, stack, p -> Fluids.contains(p.getContents(), fluidStack));
    }

    @Nullable
    public static FluidStack getFluidStackInContainer(@Nullable ItemStack stack) {
        return FluidUtil.getFluidContained(stack);
    }

    @Nullable
    public static Fluid getFluidInContainer(@Nullable ItemStack stack) {
        return Optional.ofNullable(FluidUtil.getFluidContained(stack)).map(FluidStack::getFluid).orElse(null);
    }

    public static class FillReturn {
        public final ItemStack container;
        public final int amount;

        public FillReturn(@Nullable ItemStack con, int amount) {
            this.container = con;
            this.amount = amount;
        }
    }

    public static class DrainReturn {
        public final ItemStack container;
        public final FluidStack fluidDrained;
        public final boolean isAtomic;

        public DrainReturn(@Nullable ItemStack con, @Nullable FluidStack fluidDrained, boolean isAtomic) {
            this.container = con;
            this.fluidDrained = fluidDrained;
            this.isAtomic = isAtomic;
        }
    }
}
