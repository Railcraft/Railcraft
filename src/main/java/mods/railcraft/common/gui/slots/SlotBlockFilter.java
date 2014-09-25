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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SlotBlockFilter extends SlotRailcraft {

    public SlotBlockFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setPhantom();
        setStackLimit(1);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock)
            return true;
        return false;
    }

}
