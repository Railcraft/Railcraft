/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.multi.TileTankWater;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerTankWater extends RailcraftContainer {

    public ContainerTankWater(InventoryPlayer inventoryplayer, TileTankWater tile) {

        tile.getLogic(IFluidHandlerImplementor.class).ifPresent(tank -> {
            addWidget(new FluidGaugeWidget(tank.getTankManager().get(0), 35, 20, 176, 0, 48, 47));
        });

        tile.getLogic(IInventoryImplementor.class).ifPresent(inv -> {
            addSlot(new SlotRailcraft(inv.getInventory(), 0, 116, 18));
            addSlot(new SlotOutput(inv.getInventory(), 1, 140, 36));
            addSlot(new SlotOutput(inv.getInventory(), 2, 116, 54));
        });

        addPlayerSlots(inventoryplayer);
    }
}
