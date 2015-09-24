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
import net.minecraft.inventory.Slot;
import mods.railcraft.common.util.inventory.InvTools;

public class SlotLinked extends Slot {

    private Slot masterSlot;
    private boolean allowNull = false;

    public SlotLinked(IInventory iinventory, int slotIndex, int posX, int posY, Slot masterSlot) {
        super(iinventory, slotIndex, posX, posY);
        this.masterSlot = masterSlot;
    }

    public SlotLinked setAllowNull() {
        allowNull = true;
        return this;
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        ItemStack master = masterSlot.getStack();
        if (master == null)
            return allowNull;
        return InvTools.isItemEqual(stack, master);
    }
}
