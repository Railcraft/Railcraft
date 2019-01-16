/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.multi.TileSteamTurbine;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.gui.widgets.AnalogWidget;
import mods.railcraft.common.gui.widgets.ChargeNetworkIndicator;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerTurbine extends RailcraftContainer {

    public static final int GUI_HEIGHT = 140;
    public final TileSteamTurbine tile;
    private int lastOutput;

    public ContainerTurbine(InventoryPlayer inventoryplayer, TileSteamTurbine tile) {
        super(tile.getInventory());
        this.tile = tile;

        addWidget(new AnalogWidget(new ChargeNetworkIndicator(tile.getWorld(), tile.getPos()), 110, 38, 28, 14, 99, 65));

        addSlot(new SlotStackFilter(StackFilters.of(RailcraftItems.TURBINE_ROTOR), tile.getInventory(), 0, 60, 24)
                .setStackLimit(1));

        addPlayerSlots(inventoryplayer, GUI_HEIGHT);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        for (IContainerListener listener : listeners) {
            if (lastOutput != Math.round(tile.output))
                listener.sendWindowProperty(this, 0, Math.round(tile.output));
        }
        lastOutput = Math.round(tile.output);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, Math.round(tile.output));
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            tile.output = data;
        }
    }

    private class SlotTurbine extends SlotRailcraft {

        public SlotTurbine(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
            setStackLimit(1);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return RailcraftItems.TURBINE_ROTOR.isEqual(stack);
        }

    }
}
