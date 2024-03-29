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
import mods.railcraft.common.carts.EntityCartUndercutter;
import mods.railcraft.common.gui.slots.SlotBlockFilter;
import mods.railcraft.common.gui.slots.SlotLinked;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCartUndercutter extends RailcraftContainer {

    public ContainerCartUndercutter(InventoryPlayer inventoryplayer, CartBaseMaintenancePattern cart) {
        super(cart);

        addSlot(new SlotBlockFilter(cart.getPattern(), 0, 17, 36));
        addSlot(new SlotBlockFilter(cart.getPattern(), 1, 35, 36));
        addSlot(new SlotBlockFilter(cart.getPattern(), 2, 17, 78));
        addSlot(new SlotBlockFilter(cart.getPattern(), 3, 35, 78));
        Slot under;
        addSlot(under = new SlotUndercutterFilter(cart.getPattern(), 4, 80, 36));
        Slot side;
        addSlot(side = new SlotUndercutterFilter(cart.getPattern(), 5, 80, 78));
        addSlot(new SlotLinked(cart, 0, 131, 36, under));
        addSlot(new SlotLinked(cart, 1, 131, 78, side));

        addPlayerSlots(inventoryplayer, 205);
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
