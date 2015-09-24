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
import mods.railcraft.common.carts.EntityCartTrackRelayer;
import mods.railcraft.common.gui.slots.SlotLinked;
import mods.railcraft.common.gui.slots.SlotTrackFilter;

public class ContainerCartTrackRelayer extends RailcraftContainer {

    private EntityCartTrackRelayer cart;
    private Slot track;

    public ContainerCartTrackRelayer(InventoryPlayer inventoryplayer, EntityCartTrackRelayer cart) {
        super(cart);
        this.cart = cart;
        addSlot(new SlotTrackFilter(cart.getPattern(), 0, 26, 43));
        addSlot(track = new SlotTrackFilter(cart.getPattern(), 1, 71, 43));
        addSlot(new SlotLinked(cart, 0, 130, 43, track));
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }
//    @Override
//    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
//        ItemStack itemstack = null;
//        Slot slot = (Slot) inventorySlots.get(i);
//        if (slot != null && slot.getHasStack()) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//            if (i >= 3 && i < 39 && copySlot.isItemValid(itemstack1)) {
//                if (!mergeItemStack(itemstack1, 2, 3, false)) {
//                    return null;
//                }
//            } else if (i >= 3 && i < 30) {
//                if (!mergeItemStack(itemstack1, 29, 38, false)) {
//                    return null;
//                }
//            } else if (i >= 30 && i < 39) {
//                if (!mergeItemStack(itemstack1, 3, 29, false)) {
//                    return null;
//                }
//            } else if (!mergeItemStack(itemstack1, 3, 38, false)) {
//                return null;
//            }
//            if (itemstack1.stackSize == 0) {
//                slot.putStack(null);
//            } else {
//                slot.onSlotChanged();
//            }
//            if (itemstack1.stackSize != itemstack.stackSize) {
//                slot.onPickupFromSlot(player, itemstack1);
//            } else {
//                return null;
//            }
//        }
//        return itemstack;
//    }
}
