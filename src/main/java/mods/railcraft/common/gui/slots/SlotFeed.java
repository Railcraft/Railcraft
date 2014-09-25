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
import mods.railcraft.common.util.inventory.filters.StackFilter;

public class SlotFeed extends Slot
{

    public SlotFeed(IInventory iinventory, int slotIndex, int posX, int posY)
    {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return canPlaceItem(stack);
    }

    public static boolean canPlaceItem(ItemStack stack)
    {
        return  StackFilter.FEED.matches(stack);
    }
}
