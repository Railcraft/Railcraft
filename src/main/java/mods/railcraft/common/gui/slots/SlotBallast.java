/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotBallast extends SlotRailcraft {

    public SlotBallast(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack != null && BallastRegistry.isItemBallast(stack);
    }

}
