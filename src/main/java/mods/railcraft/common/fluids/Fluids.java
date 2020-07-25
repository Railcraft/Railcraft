/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids implements Supplier<@Nullable Fluid> {

    WATER, LAVA, FUEL, BIOFUEL, IC2BIOGAS, CREOSOTE, STEAM, BIOETHANOL("bio.ethanol"), COAL, PYROTHEUM, FRESHWATER, BIODIESEL, DIESEL, GASOLINE,
    OIL_HEAVY, OIL_DENSE, OIL_DISTILLED, FUEL_DENSE, FUEL_MIXED_HEAVY, FUEL_LIGHT, FUEL_MIXED_LIGHT, FUEL_GASEOUS;
    private final String tag;

    Fluids() {
        tag = name().toLowerCase(Locale.ROOT);
    }

    Fluids(String fluidName) {
        tag = fluidName;
    }

    public static boolean areEqual(@Nullable FluidStack fluidStack1, @Nullable FluidStack fluidStack2) {
        return fluidStack1 != null && fluidStack1.isFluidEqual(fluidStack2);
    }

    public static boolean areEqual(@Nullable Fluid fluid, @Nullable FluidStack fluidStack) {
        return (fluidStack != null && fluid == fluidStack.getFluid()) || (fluid == null && fluidStack == null);
    }

    public static boolean areIdentical(@Nullable FluidStack fluidStackA, @Nullable FluidStack fluidStackB) {
        return fluidStackA == fluidStackB || fluidStackA != null && fluidStackA.isFluidStackIdentical(fluidStackB);
    }

    public static boolean contains(@Nullable FluidStack pool, @Nullable FluidStack request) {
        return pool == request || (pool != null && (request == null || FluidTools.matches(pool, request) && pool.amount >= request.amount));
    }

    @Contract("null -> true")
    public static boolean isEmpty(@Nullable FluidStack fluidStack) {
        return fluidStack == null || fluidStack.amount <= 0;
    }

    @Contract("null -> false")
    public static boolean nonEmpty(@Nullable FluidStack fluidStack) {
        return fluidStack != null && fluidStack.amount > 0;
    }

    public static @Nullable FluidStack copy(@Nullable FluidStack fluidStack) {
        return fluidStack == null ? null : fluidStack.copy();
    }

    public String getTag() {
        return tag;
    }

    @Override
    public @Nullable Fluid get() {
        return FluidRegistry.getFluid(tag);
    }

    public Optional<Fluid> object() {
        return Optional.ofNullable(get());
    }

    public boolean isPresent() {
        return get() != null;
    }

    public void ifPresent(Consumer<Fluid> consumer) {
        Fluid fluid = get();
        if (fluid != null)
            consumer.accept(fluid);
    }

    public <U> Optional<U> map(Function<Fluid, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Optional.empty();
        else {
            return Optional.ofNullable(mapper.apply(get()));
        }
    }

    /**
     * Gets a FluidStack filled with qty milliBuckets worth of Fluid.
     */
    public @Nullable FluidStack get(int qty) {
        return FluidRegistry.getFluidStack(tag, qty);
    }

    /**
     * Gets a FluidStack filled with n buckets worth of Fluid.
     */
    public @Nullable FluidStack getB(int n) {
        return FluidRegistry.getFluidStack(tag, n * FluidTools.BUCKET_VOLUME);
    }

    /**
     * Gets a FluidStack filled with one buckets worth of Fluid.
     */
    public @Nullable FluidStack getBucket() {
        return FluidRegistry.getFluidStack(tag, FluidTools.BUCKET_VOLUME);
    }

    public boolean is(@Nullable Fluid fluid) {
        return fluid != null && get() == fluid;
    }

    public boolean is(@Nullable FluidStack fluidStack) {
        return fluidStack != null && is(fluidStack.getFluid());
    }

    public boolean is(@Nullable Block fluidBlock) {
        return fluidBlock != null && is(FluidTools.getFluid(fluidBlock));
    }

    public boolean isContained(ItemStack containerStack) {
        Fluid fluid = get();
        return fluid != null && !InvTools.isEmpty(containerStack) && FluidItemHelper.containsFluid(containerStack, fluid);
    }

}
