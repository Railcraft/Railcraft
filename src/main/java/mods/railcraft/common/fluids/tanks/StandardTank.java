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
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardTank extends FluidTank {
    public static final int DEFAULT_COLOR = 0xFFFFFF;
    protected final ToolTip toolTip = new ToolTip() {
        @Override
        public void refresh() {
            refreshTooltip();
        }
    };
    private int tankIndex;
    private boolean hidden;
    @Nullable
    private Consumer<StandardTank> updateCallback;

    public StandardTank(int capacity) {
        super(capacity);
    }

    public StandardTank(int capacity, @Nullable TileEntity tile) {
        this(capacity);
        this.tile = tile;
    }

    public void setUpdateCallback(Consumer<StandardTank> callback) {
        this.updateCallback = callback;
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

    @Nullable
    public Fluid getFluidType() {
        return getFluid() != null ? getFluid().getFluid() : null;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return super.canFillFluidType(fluid) && matchesFilter(fluid);
    }

    public boolean matchesFilter(@Nullable FluidStack fluidStack) {
        return true;
    }

    @Override
    public void setFluid(@Nullable FluidStack resource) {
        if (!matchesFilter(resource))
            return;
        super.setFluid(resource);
        if (updateCallback != null)
            updateCallback.accept(this);
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill) {
        if (!matchesFilter(resource))
            return 0;
        int ret = super.fillInternal(resource, doFill);
        if (ret != 0 && updateCallback != null)
            updateCallback.accept(this);
        return ret;
    }

    @Override
    public int fill(final FluidStack resource, final boolean doFill) {
        if (resource == null)
            return 0;
        if (resource.amount <= 0)
            return 0;
        int ret = super.fill(resource, doFill);
        if (ret != 0 && updateCallback != null)
            updateCallback.accept(this);
        return ret;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0)
            return null;
        FluidStack ret = super.drain(maxDrain, doDrain);
        if (ret != null && updateCallback != null)
            updateCallback.accept(this);
        return ret;
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    protected void refreshTooltip() {
        toolTip.clear();
        int amount = getFluidAmount();
        FluidStack fluidStack = getFluid();
        if (!Fluids.isEmpty(fluidStack)) {
            toolTip.add(getFluidNameToolTip(fluidStack.getFluid()));
        }
        toolTip.add(new ToolTipLine(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity())));
    }

    protected ToolTipLine getFluidNameToolTip(Fluid fluid) {
        EnumRarity rarity = fluid.getRarity();
        if (rarity == null)
            rarity = EnumRarity.COMMON;
        ToolTipLine fluidName = new ToolTipLine(fluid.getLocalizedName(getFluid()), rarity.rarityColor);
        fluidName.setSpacing(2);
        return fluidName;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
