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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotStackFilter extends SlotRailcraft {

    private final IStackFilter filter;

    public SlotStackFilter(IStackFilter filter, IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        this.filter = filter;
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return filter.matches(stack);
    }
}
