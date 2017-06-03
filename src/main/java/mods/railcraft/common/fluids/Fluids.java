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
import net.minecraft.block.Block;
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
        tag = name().toLowerCase(Locale.ROOT);
    }

    public static boolean areEqual(@Nullable Fluid fluid, @Nullable FluidStack fluidStack) {
        return (fluidStack != null && fluid == fluidStack.getFluid()) || (fluid == null && fluidStack == null);
    }

    public static boolean areIdentical(@Nullable FluidStack fluidStackA, @Nullable FluidStack fluidStackB) {
        return fluidStackA == fluidStackB || fluidStackA != null && fluidStackA.isFluidStackIdentical(fluidStackB);
    }

    public static boolean contains(@Nullable FluidStack pool, @Nullable FluidStack request) {
        return pool == request || (pool != null && (request == null || pool.amount >= request.amount));
    }

    public static boolean isEmpty(@Nullable FluidStack fluidStack) {
        return fluidStack == null || fluidStack.amount <= 0;
    }

    public static boolean isNotEmpty(@Nullable FluidStack fluidStack) {
        return fluidStack != null && fluidStack.amount > 0;
    }

    @Nullable
    public static FluidStack copy(@Nullable FluidStack fluidStack) {
        return fluidStack == null ? null : fluidStack.copy();
    }

    public String getTag() {
        return tag;
    }

    @Nullable
    public Fluid get() {
        return FluidRegistry.getFluid(tag);
    }

    /**
     * Gets a FluidStack filled with qty milliBuckets worth of Fluid.
     */
    @Nullable
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

    public boolean is(@Nullable FluidStack fluidStack) {
        return fluidStack != null && get() == fluidStack.getFluid();
    }

    public boolean is(@Nullable Block fluidBlock) {
        return fluidBlock != null && get() == FluidTools.getFluid(fluidBlock);
    }

    public boolean isContained(ItemStack containerStack) {
        return !InvTools.isEmpty(containerStack) && FluidItemHelper.containsFluid(containerStack, get());
    }

}
