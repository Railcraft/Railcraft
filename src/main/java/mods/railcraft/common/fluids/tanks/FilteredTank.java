/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    private Supplier<Fluid> filter;

    public FilteredTank(int capacity) {
        this(capacity, (TileEntity) null);
    }

    public FilteredTank(int capacity, Fluid filter) {
        this(capacity, filter, null);
    }

    public FilteredTank(int capacity, Fluid filter, @Nullable TileEntity tile) {
        this(capacity, tile);
        setFilter(() -> filter);
    }

    public FilteredTank(int capacity, @Nullable TileEntity tile) {
        super(capacity, tile);
    }

    public void setFilter(Supplier<Fluid> filter) {
        this.filter = filter;
    }

    @Override
    public boolean matchesFilter(FluidStack fluidStack) {
        return filter == null || filter.get() == null || Fluids.areEqual(filter.get(), fluid);
    }

    @Override
    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        Fluid filterFluid = filter.get();
        if (filterFluid != null) {
            EnumRarity rarity = filterFluid.getRarity();
            if (rarity == null)
                rarity = EnumRarity.COMMON;
            ToolTipLine name = new ToolTipLine(filterFluid.getLocalizedName(getFluid()), rarity.rarityColor);
            name.setSpacing(2);
            toolTip.add(name);
            FluidStack fluidStack = getFluid();
            if (fluidStack != null && fluidStack.amount > 0)
                amount = fluidStack.amount;
        }
        toolTip.add(new ToolTipLine(String.format("%,d", amount) + " / " + String.format("%,d", getCapacity())));
    }

}
