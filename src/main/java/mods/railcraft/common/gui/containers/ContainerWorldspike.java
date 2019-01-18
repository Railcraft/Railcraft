/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.slots.SlotIngredientMap;
import mods.railcraft.common.util.misc.IWorldspike;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerWorldspike extends RailcraftContainer {

    public static final int GUI_HEIGHT = 140;
    private final IWorldspike worldspike;
    public short minutesRemaining;
    private short prevMinutesRemaining;

    public ContainerWorldspike(InventoryPlayer inventoryplayer, IWorldspike a) {
        super(a);
        this.worldspike = a;
        addSlot(new SlotIngredientMap<>(worldspike.getFuelMap(), worldspike, 0, 60, 24).setStackLimit(16));

        addPlayerSlots(inventoryplayer, GUI_HEIGHT);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, getMinutesRemaining(worldspike.getFuelAmount()));
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
        short minutes = getMinutesRemaining(worldspike.getFuelAmount());

        for (IContainerListener listener : listeners) {
            if (prevMinutesRemaining != minutes)
                listener.sendWindowProperty(this, 0, minutes);
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
