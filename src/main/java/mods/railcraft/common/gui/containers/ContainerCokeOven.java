/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
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

public class ContainerCokeOven extends ContainerLogic {

    public ContainerCokeOven(InventoryPlayer invPlayer, CokeOvenLogic logic) {
        super(logic);

        addWidget(new FluidGaugeWidget(logic.getTank(), 90, 24, 176, 0, 48, 47));

        addSlot(new SlotRailcraft(logic, CokeOvenLogic.SLOT_INPUT, 16, 43));
        addSlot(new SlotOutput(logic, CokeOvenLogic.SLOT_OUTPUT, 62, 43));
        addSlot(new SlotFluidContainerEmpty(logic, CokeOvenLogic.SLOT_INPUT_FLUID, 149, 22));
        addSlot(new SlotOutput(logic, CokeOvenLogic.SLOT_PROCESS_FLUID, 149, 40));
        addSlot(new SlotOutput(logic, CokeOvenLogic.SLOT_OUTPUT_FLUID, 149, 57));

        addPlayerSlots(invPlayer);
    }
}
