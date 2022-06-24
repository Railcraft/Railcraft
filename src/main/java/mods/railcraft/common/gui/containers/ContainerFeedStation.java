/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.equipment.TileFeedStation;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerFeedStation extends RailcraftContainer {

    public ContainerFeedStation(InventoryPlayer inventoryplayer, TileFeedStation tile) {
        super(tile);
        addSlot(new SlotRailcraft(tile, 0, 60, 24).setFilter(StackFilters.FEED));
        addPlayerSlots(inventoryplayer, 140);
    }
}
