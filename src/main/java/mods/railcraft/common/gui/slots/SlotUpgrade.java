/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {

    public SlotUpgrade(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        ItemStack storage = IC2Plugin.getItem("upgrade#energy_storage");
        ItemStack overclocker = IC2Plugin.getItem("upgrade#overclocker");
        ItemStack transformer = IC2Plugin.getItem("upgrade#transformer");
        Item lapotron = RailcraftItems.LAPOTRON_UPGRADE.item();

        if (!InvTools.isEmpty(stack))
            if (!InvTools.isEmpty(storage) && stack.isItemEqual(storage))
                return true;
            else if (!InvTools.isEmpty(overclocker) && stack.isItemEqual(overclocker))
                return true;
            else if (!InvTools.isEmpty(transformer) && stack.isItemEqual(transformer))
                return true;
            else return lapotron != null && stack.getItem() == lapotron;
        return false;
    }

}
