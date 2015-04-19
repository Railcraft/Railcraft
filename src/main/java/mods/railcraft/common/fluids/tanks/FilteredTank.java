/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    private final Fluid filter;

    public FilteredTank(int capacity, Fluid filter) {
        this(capacity, filter, null);
    }

    public FilteredTank(int capacity, Fluid filter, TileEntity tile) {
        super(capacity, tile);
        this.filter = filter;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (liquidMatchesFilter(resource))
            return super.fill(resource, doFill);
        return 0;
    }

    public Fluid getFilter() {
        return filter;
    }

    public boolean liquidMatchesFilter(FluidStack resource) {
        if (resource == null || filter == null)
            return false;
        return resource.getFluid() == filter;
    }

    @Override
    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        if (filter != null) {
            EnumRarity rarity = filter.getRarity();
            if (rarity == null)
                rarity = EnumRarity.common;
            ToolTipLine name = new ToolTipLine(filter.getLocalizedName(getFluid()), rarity.rarityColor);
            name.setSpacing(2);
            toolTip.add(name);
            if (renderData.fluid != null && renderData.amount > 0)
                amount = renderData.amount;
        }
        toolTip.add(new ToolTipLine(String.format("%,d", amount) + " / " + String.format("%,d", getCapacity())));
    }

}
