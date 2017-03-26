/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.misc.IAnchor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAnchor extends RailcraftContainer {

    private final IAnchor anchor;
    public short minutesRemaining;
    private short prevMinutesRemaining;

    public ContainerAnchor(InventoryPlayer inventoryplayer, IAnchor a) {
        super(a);
        this.anchor = a;
        addSlot(new SlotStackFilter(stack -> anchor.getFuelMap().containsKey(stack), anchor, 0, 60, 24));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendProgressBarUpdate(this, 0, getMinutesRemaining(anchor.getAnchorFuel()));
    }

    private short getMinutesRemaining(long fuel) {
        return (short) Math.ceil((double) fuel / RailcraftConstants.TICKS_PER_MIN);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        short minutes = getMinutesRemaining(anchor.getAnchorFuel());

        for (IContainerListener listener : listeners) {
            if (prevMinutesRemaining != minutes)
                listener.sendProgressBarUpdate(this, 0, minutes);
        }

        this.prevMinutesRemaining = minutes;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0)
            minutesRemaining = (short) value;
    }

}
