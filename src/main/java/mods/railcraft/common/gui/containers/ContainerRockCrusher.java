/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.RockCrusherLogic;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.AnalogWidget;
import mods.railcraft.common.gui.widgets.ChargeNetworkIndicator;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerRockCrusher extends ContainerCrafter {
    public static final int GUI_HEIGHT = 171;

    public ContainerRockCrusher(InventoryPlayer invPlayer, RockCrusherLogic crusher) {
        super(crusher);
        addWidget(new AnalogWidget(new ChargeNetworkIndicator(logic.theWorldAsserted(), logic.getPos()), 74, 59, 28, 14, 99, 65));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotRailcraft(crusher, i * 3 + k, 17 + k * 18, 21 + i * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotOutput(crusher, 9 + i * 3 + k, 107 + k * 18, 21 + i * 18));
            }
        }

        addPlayerSlots(invPlayer, GUI_HEIGHT);
    }
}
