/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

    WATER, LAVA, FUEL, BIOFUEL, CREOSOTE, STEAM, BIOETHANOL, COAL, PYROTHEUM, FRESHWATER;
    private final String tag;

    Fluids() {
        tag = name().toLowerCase(Locale.ENGLISH);
    }

    public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
        if (fluidStack != null && fluid == fluidStack.getFluid())
            return true;
        return fluid == null && fluidStack == null;
    }

    public static boolean areIdentical(@Nullable FluidStack fluidStackA, @Nullable FluidStack fluidStackB) {
        return fluidStackA == fluidStackB || fluidStackA != null && fluidStackA.isFluidStackIdentical(fluidStackB);
    }

    public String getTag() {
        return tag;
    }

    public Fluid get() {
        return FluidRegistry.getFluid(tag);
    }

    /**
     * Gets a FluidStack filled with qty milliBuckets worth of Fluid.
     */
    public FluidStack get(int qty) {
        return FluidRegistry.getFluidStack(tag, qty);
    }

    /**
     * Gets a FluidStack filled with n buckets worth of Fluid.
     */
    public FluidStack getB(int n) {
        return FluidRegistry.getFluidStack(tag, n * FluidContainerRegistry.BUCKET_VOLUME);
    }

    /**
     * Gets a FluidStack filled with one buckets worth of Fluid.
     */
    public FluidStack getBucket() {
        return FluidRegistry.getFluidStack(tag, FluidContainerRegistry.BUCKET_VOLUME);
    }

    public boolean is(Fluid fluid) {
        return get() == fluid;
    }

    public boolean is(FluidStack fluidStack) {
        return fluidStack != null && get() == fluidStack.getFluid();
    }

    public boolean isContained(ItemStack containerStack) {
        return containerStack != null && FluidItemHelper.containsFluid(containerStack, get());
    }

}
