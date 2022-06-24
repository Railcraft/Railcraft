/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.CartBaseMaintenancePattern;
import mods.railcraft.common.gui.slots.SlotLinked;
import mods.railcraft.common.gui.slots.SlotTrackFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCartTrackRelayer extends RailcraftContainer {

    public ContainerCartTrackRelayer(InventoryPlayer inventoryplayer, CartBaseMaintenancePattern cart) {
        super(cart);
        addSlot(new SlotTrackFilter(cart.getPattern(), 0, 26, 43));
        Slot track;
        addSlot(track = new SlotTrackFilter(cart.getPattern(), 1, 71, 43));
        addSlot(new SlotLinked(cart, 0, 130, 43, track));

        addPlayerSlots(inventoryplayer);
    }
}
