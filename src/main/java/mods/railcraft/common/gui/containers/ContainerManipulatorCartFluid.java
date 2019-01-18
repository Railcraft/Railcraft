/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.gui.slots.SlotFluidFilter;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerManipulatorCartFluid extends ContainerManipulatorCart<TileFluidManipulator> {

    public ContainerManipulatorCartFluid(InventoryPlayer player, TileFluidManipulator tile) {
        super(player, tile);
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 17, 21, 176, 0, 16, 47));
    }

    @Override
    protected void addSlots(TileFluidManipulator tile) {
        addSlot(new SlotFluidFilter(tile.getFluidFilter(), 0, 116, 26));
        addSlot(new SlotRailcraft(tile, 0, 152, 26));
        addSlot(new SlotOutput(tile, 1, 152, 62));
        addSlot(new SlotOutput(tile, 2, 116, 62));
    }
}
