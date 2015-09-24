/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.carts.EntityCartUndercutter;
import mods.railcraft.common.gui.slots.SlotBlockFilter;
import mods.railcraft.common.gui.slots.SlotLinked;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerCartUndercutter extends RailcraftContainer {

    private EntityCartUndercutter cart;
    private Slot under;
    private Slot side;

    public ContainerCartUndercutter(InventoryPlayer inventoryplayer, EntityCartUndercutter cart) {
        super(cart);
        this.cart = cart;

        addSlot(new SlotBlockFilter(cart.getPattern(), 0, 17, 45));
        addSlot(new SlotBlockFilter(cart.getPattern(), 1, 35, 45));
        addSlot(new SlotBlockFilter(cart.getPattern(), 2, 17, 87));
        addSlot(new SlotBlockFilter(cart.getPattern(), 3, 35, 87));
        addSlot(under = new SlotUndercutterFilter(cart.getPattern(), 4, 80, 45));
        addSlot(side = new SlotUndercutterFilter(cart.getPattern(), 5, 80, 87));
        addSlot(new SlotLinked(cart, 0, 131, 45, under));
        addSlot(new SlotLinked(cart, 1, 131, 87, side));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 123 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 181));
        }
    }

    private class SlotUndercutterFilter extends SlotBlockFilter {

        public SlotUndercutterFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return EntityCartUndercutter.isValidBallast(stack);
        }

    }
}
