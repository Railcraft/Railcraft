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
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerTank extends RailcraftContainer {

    public ContainerTank(InventoryPlayer inventoryplayer, ITankTile tile) {
        super(tile.getInventory());

        StandardTank tank = tile.getTank();
        if (tank != null) {
            addWidget(new FluidGaugeWidget(tank, 35, 23, 176, 0, 48, 47));
        }

        addSlot(new SlotRailcraft(tile.getInventory(), 0, 116, 21));
        addSlot(new SlotOutput(tile.getInventory(), 1, 116, 56));

        addPlayerSlots(inventoryplayer);
    }
}
