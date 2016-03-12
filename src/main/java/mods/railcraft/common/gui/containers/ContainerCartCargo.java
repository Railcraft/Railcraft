/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartCargo;
import mods.railcraft.common.gui.slots.SlotFilter;
import mods.railcraft.common.gui.slots.SlotLinked;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCartCargo extends RailcraftContainer {

    private EntityCartCargo cart;

    public ContainerCartCargo(InventoryPlayer inventoryplayer, EntityCartCargo cart) {
        super(cart);
        this.cart = cart;

        Slot filter = new SlotFilter(cart.getFilterInv(), 0, 25, 35).setStackLimit(1);
        addSlot(filter);

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 6; k++) {
                addSlot(new SlotLinked(cart, k + i * 6, 62 + k * 18, 18 + i * 18, filter));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }
}
