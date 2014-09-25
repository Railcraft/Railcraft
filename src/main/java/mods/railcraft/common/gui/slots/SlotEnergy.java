/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;

public class SlotEnergy extends Slot {

    public SlotEnergy(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return IC2Plugin.isEnergyItem(stack);
    }
}
