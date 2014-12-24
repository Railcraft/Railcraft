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
import mods.railcraft.common.fluids.FluidHelper;

public class SlotFluidContainerEmpty extends Slot
{

    public SlotFluidContainerEmpty(IInventory iinventory, int slotIndex, int posX, int posY)
    {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        return canPlaceItem(itemstack);
    }

    public static boolean canPlaceItem(ItemStack itemstack)
    {
        if(itemstack == null) {
            return false;
        }
        if(FluidHelper.isEmptyContainer(itemstack)) {
            return true;
        }
        return false;
    }
}
