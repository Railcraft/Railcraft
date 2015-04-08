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

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

    WATER, LAVA, FUEL, BIOFUEL, CREOSOTE, STEAM, BIOETHANOL, COAL, PYROTHEUM, FRESHWATER;
    private final String tag;

    private Fluids() {
        tag = name().toLowerCase(Locale.ENGLISH);
    }

    public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
        if (fluidStack != null && fluid == fluidStack.getFluid())
            return true;
        return fluid == null && fluidStack == null;
    }

    public String getTag() {
        return tag;
    }

    public Fluid get() {
        return FluidRegistry.getFluid(tag);
    }

    /**
     * Gets a FluidStack filled with qty milliBuckets worth of Fluid.
     *
     * @param qty
     * @return
     */
    public FluidStack get(int qty) {
        return FluidRegistry.getFluidStack(tag, qty);
    }

    /**
     * Gets a FluidStack filled with n buckets worth of Fluid.
     *
     * @param n
     * @return
     */
    public FluidStack getB(int n) {
        return FluidRegistry.getFluidStack(tag, n * FluidContainerRegistry.BUCKET_VOLUME);
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
