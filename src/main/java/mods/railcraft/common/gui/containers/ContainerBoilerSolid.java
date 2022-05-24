/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.structures.TileBoilerFireboxSolid;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerBoilerSolid extends ContainerBoiler {

    public ContainerBoilerSolid(InventoryPlayer inventoryplayer, ILogicContainer logicContainer) {
        super(inventoryplayer, logicContainer);

        addSlot(new SlotRailcraft(inv, TileBoilerFireboxSolid.SLOT_FIREBOX, 62, 39)); // Fuel
        addSlot(new SlotRailcraft(inv, TileBoilerFireboxSolid.SLOT_BUNKER_A, 89, 20)); // Fuel
        addSlot(new SlotRailcraft(inv, TileBoilerFireboxSolid.SLOT_BUNKER_B, 89, 38)); // Fuel
        addSlot(new SlotRailcraft(inv, TileBoilerFireboxSolid.SLOT_BUNKER_B, 89, 56)); // Fuel
    }

}
