/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.fluids;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 9/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OptionalFluidStack {
    private final FluidStack fluidStack;

    private OptionalFluidStack(@Nullable FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public static OptionalFluidStack of(@Nullable FluidStack fluidStack) {
        return new OptionalFluidStack(fluidStack);
    }

    public static OptionalFluidStack empty() {
        return new OptionalFluidStack(null);
    }

    @Nullable
    public FluidStack orElse(@Nullable FluidStack fluidStack) {
        if (this.fluidStack != null)
            return this.fluidStack;
        return fluidStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionalFluidStack that = (OptionalFluidStack) o;

        if (fluidStack != null ? !fluidStack.isFluidStackIdentical(that.fluidStack) : that.fluidStack != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fluidStack != null ? fluidStack.hashCode() : 0;
    }
}
