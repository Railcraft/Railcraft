/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.CokeOvenLogic;
import mods.railcraft.common.gui.slots.SlotFluidContainerEmpty;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCokeOven extends ContainerCrafter {

    public ContainerCokeOven(InventoryPlayer invPlayer, CokeOvenLogic logic) {
        super(logic);

        addWidget(new FluidGaugeWidget(logic.getTank(), 90, 24, 176, 0, 48, 47));

        addSlot(new SlotRailcraft(logic, 0, 16, 43));
        addSlot(new SlotOutput(logic, 1, 62, 43));
        addSlot(new SlotOutput(logic, 2, 149, 57));
        addSlot(new SlotFluidContainerEmpty(logic, 3, 149, 22));

        addPlayerSlots(invPlayer);
    }
}
