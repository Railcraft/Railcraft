/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerTank extends RailcraftContainer {

    public ContainerTank(InventoryPlayer inventoryplayer, ITankTile tile) {
        super(tile.getInventory());

        StandardTank tank = tile.getTank();
        if (tank != null) {
            addWidget(new FluidGaugeWidget(tank, 35, 23, 176, 0, 48, 47));
        }

        addSlot(new SlotStackFilter(tile.getInputFilter(), tile.getInventory(), 0, 116, 21));
        addSlot(new SlotOutput(tile.getInventory(), 1, 116, 56));
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }
}
