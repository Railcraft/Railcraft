/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.fluids;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class OptionalFluidStack {
    private final FluidStack fluidStack;

    private static final OptionalFluidStack EMPTY = new OptionalFluidStack(null);

    private OptionalFluidStack(@Nullable FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public static OptionalFluidStack of(@Nullable FluidStack fluidStack) {
        return fluidStack == null ? EMPTY : new OptionalFluidStack(fluidStack);
    }

    public static OptionalFluidStack empty() {
        return EMPTY;
    }

    @Contract("!null -> !null")
    public @Nullable FluidStack orElse(@Nullable FluidStack other) {
        return fluidStack != null ? fluidStack : other;
    }

    public OptionalFluidStack copy() {
        return fluidStack == null ? EMPTY : new OptionalFluidStack(fluidStack.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof OptionalFluidStack)) return false;

        OptionalFluidStack that = (OptionalFluidStack) o;
        return fluidStack != null ? fluidStack.isFluidStackIdentical(that.fluidStack) : that.fluidStack == null;
    }

    @Override
    public int hashCode() {
        return fluidStack != null ? fluidStack.hashCode() : 0;
    }
}
