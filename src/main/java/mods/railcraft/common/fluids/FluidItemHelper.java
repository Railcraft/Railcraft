/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Helper functions for Fluid Items
 *
 * Created by CovertJaguar on 4/2/2015.
 */
@SuppressWarnings("unused")
public final class FluidItemHelper {

    public static boolean isContainer(ItemStack stack) {
        return FluidUtil.getFluidHandler(stack) != null;
    }

    public static boolean testContainerProperties(boolean all, ItemStack stack, Predicate<IFluidTankProperties> test) {
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
        return FluidTools.testProperties(all, fluidHandler, test);
    }

    public static boolean isFluidInContainer(ItemStack stack) {
        return testContainerProperties(false, stack, p -> {
            FluidStack contents = p.getContents();
            return contents != null && contents.amount > 0;
        });
    }

    public static boolean isFullContainer(ItemStack stack) {
        return testContainerProperties(true, stack, p -> {
            FluidStack contents = p.getContents();
            return contents != null && contents.amount >= p.getCapacity();
        });
    }

    public static boolean isEmptyContainer(ItemStack stack) {
        return testContainerProperties(true, stack, p -> {
            FluidStack contents = p.getContents();
            return Fluids.isEmpty(contents);
        });
    }

    public static boolean isRoomInContainer(ItemStack stack) {
        return testContainerProperties(false, stack, p -> {
            FluidStack contents = p.getContents();
            return contents == null || contents.amount < p.getCapacity();
        });
    }

    public static boolean isRoomInContainer(ItemStack stack, @Nullable Fluid fluid) {
        if (fluid == null) return false;
        stack = stack.copy();
        stack.setCount(1);
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
        return fluidHandler != null && fluidHandler.fill(new FluidStack(fluid, FluidTools.BUCKET_VOLUME), false) > 0;
    }

    public static boolean containsFluid(ItemStack stack, @Nullable Fluid fluid) {
        return testContainerProperties(false, stack, p -> Fluids.areEqual(fluid, p.getContents()));
    }

    public static boolean containsFluid(ItemStack stack, @Nullable FluidStack fluidStack) {
        return testContainerProperties(false, stack, p -> Fluids.contains(p.getContents(), fluidStack));
    }

    public static @Nullable FluidStack getFluidStackInContainer(ItemStack stack) {
        return FluidUtil.getFluidContained(stack);
    }

    // Use fluidstack version
    @Deprecated // Use fluidstack version
    public static @Nullable Fluid getFluidInContainer(ItemStack stack) {
        return Optional.ofNullable(FluidUtil.getFluidContained(stack)).map(FluidStack::getFluid).orElse(null);
    }

    private FluidItemHelper() {
    }
}
