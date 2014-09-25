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

public class SlotUntouchable extends SlotRailcraft {

    public SlotUntouchable(IInventory contents, int id, int x, int y) {
        super(contents, id, x, y);
        setPhantom();
        setCanAdjustPhantom(false);
        blockShift();
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

}
