/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class SlotMinecart extends SlotRailcraft {

    public SlotMinecart(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return !InvTools.isEmpty(stack) && (stack.getItem() instanceof IMinecartItem || stack.getItem() instanceof ItemMinecart);
    }
}
