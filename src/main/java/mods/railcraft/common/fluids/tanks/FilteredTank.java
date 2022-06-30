/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.common.blocks.TileRailcraft;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    public FilteredTank(int capacity) {
        this(capacity, null);
    }

    public FilteredTank(int capacity, @Nullable TileRailcraft tile) {
        super(capacity, tile);
    }

    public FilteredTank setFilterFluid(Supplier<Optional<Fluid>> filter) {
        setFilterFluidStack(() -> filter.get().map(f -> new FluidStack(f, 1)));
        return this;
    }

    public FilteredTank setFilterFluidStack(Supplier<Optional<FluidStack>> filter) {
        this.filter = Optional.ofNullable(filter);
        return this;
    }

}
