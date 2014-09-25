/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.machine.alpha.TileFeedStation;
import mods.railcraft.common.gui.slots.SlotFeed;

public class ContainerFeedStation extends RailcraftContainer
{

    public TileFeedStation tile;
    private Slot feed;

    public ContainerFeedStation(InventoryPlayer inventoryplayer, TileFeedStation tile) {
        super(tile);
        this.tile = tile;
        addSlot(feed = new SlotFeed(tile, 0, 60, 24));
        for(int i = 0; i < 3; i++) {
            for(int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for(int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

//    @Override
//    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
//        ItemStack itemstack = null;
//        Slot slot = (Slot)inventorySlots.get(i);
//        if(slot != null && slot.getHasStack()) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//            if(i >= 1 && feed.isItemValid(itemstack1)) {
//                if(!mergeItemStack(itemstack1, 0, 1, false)) {
//                    return null;
//                }
//            } else if(i >= 1 && i < 28) {
//                if(!mergeItemStack(itemstack1, 28, 37, false)) {
//                    return null;
//                }
//            } else if(i >= 28 && i < 37) {
//                if(!mergeItemStack(itemstack1, 1, 28, false)) {
//                    return null;
//                }
//            } else if(!mergeItemStack(itemstack1, 1, 37, false)) {
//                return null;
//            }
//            if(itemstack1.stackSize == 0) {
//                slot.putStack(null);
//            } else {
//                slot.onSlotChanged();
//            }
//            if(itemstack1.stackSize != itemstack.stackSize) {
//                slot.onPickupFromSlot(player, itemstack1);
//            } else {
//                return null;
//            }
//        }
//        return itemstack;
//    }
}
