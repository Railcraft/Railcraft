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
import net.minecraft.item.Item;
import mods.railcraft.common.modules.ModuleIC2;
import mods.railcraft.common.plugins.ic2.IC2Plugin;

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
        ItemStack storage = IC2Plugin.getItem("energyStorageUpgrade");
        ItemStack overclocker = IC2Plugin.getItem("overclockerUpgrade");
        ItemStack transformer = IC2Plugin.getItem("transformerUpgrade");
        Item lapotron = ModuleIC2.lapotronUpgrade;

        if (stack != null)
            if (storage != null && stack.isItemEqual(storage))
                return true;
            else if (overclocker != null && stack.isItemEqual(overclocker))
                return true;
            else if (transformer != null && stack.isItemEqual(transformer))
                return true;
            else if (lapotron != null && stack.getItem() == lapotron)
                return true;
        return false;
    }

}
