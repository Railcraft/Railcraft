/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids.tanks;

import java.util.Locale;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank {

    public static final int DEFAULT_COLOR = 0xFFFFFF;
    public int colorCache = DEFAULT_COLOR;
    private int tankIndex;
    private boolean hidden;

    public StandardTank(int capacity) {
        super(capacity);
    }

    public StandardTank(int capacity, TileEntity tile) {
        this(capacity);
        this.tile = tile;
    }

    public void setTankIndex(int index) {
        this.tankIndex = index;
    }

    public int getTankIndex() {
        return tankIndex;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        colorCache = StandardTank.DEFAULT_COLOR;
    }

    public int getColor() {
        Fluid f = getFluidType();
        if (f == null)
            return DEFAULT_COLOR;
        return f.getColor(getFluid());
    }

    public boolean isEmpty() {
        return getFluid() == null || getFluid().amount <= 0;
    }

    public boolean isFull() {
        return getFluid() != null && getFluid().amount == getCapacity();
    }

    public int getRemainingSpace() {
        return capacity - getFluidAmount();
    }

    public Fluid getFluidType() {
        return getFluid() != null ? getFluid().getFluid() : null;
    }

    @Override
    public int fill(final FluidStack resource, final boolean doFill) {
        if (resource == null)
            return 0;
        if (resource.amount <= 0)
            return 0;
        return super.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0)
            return null;
        return super.drain(maxDrain, doDrain);
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        if (getFluid() != null && getFluid().amount > 0 && getFluid().getFluid() != null) {
            Fluid fluidType = getFluidType();
            EnumRarity rarity = fluidType.getRarity();
            if (rarity == null)
                rarity = EnumRarity.common;
            ToolTipLine fluidName = new ToolTipLine(fluidType.getLocalizedName(fluid), rarity.rarityColor);
            fluidName.setSpacing(2);
            toolTip.add(fluidName);
            amount = getFluid().amount;
        }
        toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity())));
    }

    protected final ToolTip toolTip = new ToolTip() {
        @Override
        public void refresh() {
            refreshTooltip();
        }

    };

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
