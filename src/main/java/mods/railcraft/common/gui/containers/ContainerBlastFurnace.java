/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.BlastFurnaceLogic;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import net.minecraft.entity.player.InventoryPlayer;

public final class ContainerBlastFurnace extends ContainerLogic {

    public ContainerBlastFurnace(InventoryPlayer invPlayer, ILogicContainer logicContainer) {
        super(logicContainer);
        BlastFurnaceLogic blastFurnace = Logic.get(BlastFurnaceLogic.class, logicContainer);
        addSlot(new SlotStackFilter(BlastFurnaceLogic.INPUT_FILTER, blastFurnace, BlastFurnaceLogic.SLOT_INPUT, 56, 17));
        addSlot(new SlotStackFilter(BlastFurnaceLogic.FUEL_FILTER, blastFurnace, BlastFurnaceLogic.SLOT_FUEL, 56, 53));
        addSlot(new SlotOutput(blastFurnace, BlastFurnaceLogic.SLOT_OUTPUT, 116, 21));
        addSlot(new SlotOutput(blastFurnace, BlastFurnaceLogic.SLOT_SLAG, 116, 53));

        addPlayerSlots(invPlayer);
    }

}
