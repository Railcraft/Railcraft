/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids.tanks;

import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank {
    public static final int DEFAULT_COLOR = 0xFFFFFF;
    public final TankRenderData renderData = new TankRenderData();
    protected final ToolTip toolTip = new ToolTip() {
        @Override
        public void refresh() {
            refreshTooltip();
        }
    };
    private int tankIndex;
    private boolean hidden;

    public StandardTank(int capacity) {
        super(capacity);
    }

    public StandardTank(int capacity, TileEntity tile) {
        this(capacity);
        this.tile = tile;
    }

    public int getTankIndex() {
        return tankIndex;
    }

    public void setTankIndex(int index) {
        this.tankIndex = index;
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
        if (renderData.fluid != null && renderData.amount > 0) {
            EnumRarity rarity = renderData.fluid.getRarity();
            if (rarity == null)
                rarity = EnumRarity.common;
            ToolTipLine fluidName = new ToolTipLine(renderData.fluid.getLocalizedName(new FluidStack(renderData.fluid, renderData.amount)), rarity.rarityColor);
            fluidName.setSpacing(2);
            toolTip.add(fluidName);
            amount = renderData.amount;
        }
        toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity())));
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static class TankRenderData {
        public Fluid fluid = null;
        public int amount = 0;
        public int color = DEFAULT_COLOR;

        public void reset() {
            fluid = null;
            amount = 0;
            color = DEFAULT_COLOR;
        }
    }
}
