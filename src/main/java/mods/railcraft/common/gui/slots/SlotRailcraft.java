/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotRailcraft extends Slot {

    protected @Nullable ToolTip toolTips;
    protected boolean isPhantom;
    protected boolean canAdjustPhantom = true;
    protected boolean canShift = true;
    protected int stackLimit = -1;
    public BooleanSupplier isEnabled = () -> true;

    public SlotRailcraft(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return isEnabled.getAsBoolean() && inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

    public SlotRailcraft setEnableCheck(BooleanSupplier isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    /**
     * @return the toolTips
     */
    public @Nullable ToolTip getToolTip() {
        return toolTips;
    }

    /**
     * @param toolTips the tooltips to set
     */
    public void setToolTips(@Nullable ToolTip toolTips) {
        this.toolTips = toolTips;
    }

    public SlotRailcraft setPhantom() {
        isPhantom = true;
        return this;
    }

    public SlotRailcraft blockShift() {
        canShift = false;
        return this;
    }

    public SlotRailcraft setCanAdjustPhantom(boolean canAdjust) {
        this.canAdjustPhantom = canAdjust;
        return this;
    }

    public SlotRailcraft setCanShift(boolean canShift) {
        this.canShift = canShift;
        return this;
    }

    public SlotRailcraft setStackLimit(int limit) {
        this.stackLimit = limit;
        return this;
    }

    @Override
    public final int getSlotStackLimit() {
        int max = super.getSlotStackLimit();
        return stackLimit < 0 ? max : Math.min(max, stackLimit); // issue #1347
    }

    public boolean isPhantom() {
        return isPhantom;
    }

    public boolean canAdjustPhantom() {
        return canAdjustPhantom;
    }

    @Override
    public boolean canTakeStack(EntityPlayer stack) {
        return !isPhantom();
    }

    public boolean canShift() {
        return canShift;
    }

}
