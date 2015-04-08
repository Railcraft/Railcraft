/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.fluids.FluidItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFluidContainerFilled extends Slot
{

    public SlotFluidContainerFilled(IInventory iinventory, int slotIndex, int posX, int posY)
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
        if(FluidItemHelper.getFluidStackInContainer(itemstack) != null) {
            return true;
        }
        return false;
    }
}
