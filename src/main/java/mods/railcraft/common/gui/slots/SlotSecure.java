/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.api.core.items.IStackFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotSecure extends SlotStackFilter {

    public boolean locked = true;

    public SlotSecure(IStackFilter filter, IInventory contents, int id, int x, int y) {
        super(filter, contents, id, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !locked && super.isItemValid(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return !locked;
    }

    @Override
    public boolean canShift() {
        return !locked;
    }

}
