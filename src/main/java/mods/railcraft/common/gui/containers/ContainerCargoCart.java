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
import mods.railcraft.common.gui.slots.SlotFluidFilter;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCargoCart extends RailcraftContainer {

    private EntityCartCargo cart;

    public ContainerCargoCart(InventoryPlayer inventoryplayer, EntityCartCargo cart) {
        super(cart);
        this.cart = cart;

        addSlot(new SlotFilter(cart.getFilter(), 0, 25, 35));

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
