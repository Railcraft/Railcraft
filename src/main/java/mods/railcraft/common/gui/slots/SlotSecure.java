/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class SlotSecure extends SlotStackFilter {

    public boolean locked = true;

    public SlotSecure(Predicate<ItemStack> filter, IInventory contents, int id, int x, int y) {
        super(filter, contents, id, x, y);
        setStackLimit(1);
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
