/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartCargo;
import mods.railcraft.common.gui.slots.SlotLinked;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCartCargo extends RailcraftContainer {

    public ContainerCartCargo(InventoryPlayer inventoryplayer, EntityCartCargo cart) {
        super(cart);

        SlotRailcraft filter = SlotRailcraft.singleItemPhantom(cart.getFilterInv(), 0, 26, 36)
                .setFilter(StackFilters.CARGO).setEnableCheck(cart::isEmptied);
        addSlot(filter);

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 6; k++) {
                addSlot(new SlotLinked(cart, k + i * 6, 62 + k * 18, 18 + i * 18, filter));
            }
        }

        addPlayerSlots(inventoryplayer);
    }
}
