/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotPassThrough extends SlotRailcraft {

    public SlotPassThrough(IInventory inv, int slotIndex, int posX, int posY) {
        super(inv, slotIndex, posX, posY);
    }

    @Override
    public int getSlotStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return inventory.isItemValidForSlot(slotNumber, stack);
    }

}
