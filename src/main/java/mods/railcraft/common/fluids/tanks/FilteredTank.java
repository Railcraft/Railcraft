/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    private Predicate<@Nullable FluidStack> filter = Predicates.alwaysTrue();

    public FilteredTank(int capacity) {
        this(capacity, null);
    }

    public FilteredTank(int capacity, @Nullable TileEntity tile) {
        super(capacity, tile);
    }

    public void setFilter(@Nullable Predicate<@Nullable FluidStack> filter) {
        this.filter = filter == null ? Predicates.alwaysTrue() : filter;
    }

    public void setFilter(@Nullable Fluids filter) {
        this.filter = filter == null ? Predicates.alwaysTrue() : filter::is;
    }

    @Deprecated
    public void setFilter(@Nullable Supplier<@Nullable Fluid> typeFilter) {
        this.filter = typeFilter == null ? Predicates.alwaysTrue() : fluidStack -> typeFilter.get() == null || Fluids.areEqual(typeFilter.get(), fluidStack);
    }

    @Override
    public boolean matchesFilter(@Nullable FluidStack fluidStack) {
        return filter.test(fluidStack);
    }

    @Override
    protected void refreshTooltip() {
        toolTip.clear();
        int amount = getFluidAmount();
        FluidStack fluidStack = getFluid();

        if (fluidStack != null)
            toolTip.add(getFluidNameToolTip(fluidStack));

        toolTip.add(new ToolTipLine(String.format("%,d", amount) + " / " + String.format("%,d", getCapacity())));
    }

}
