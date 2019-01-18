/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotStackFilter extends SlotRailcraft {

    private final Predicate<ItemStack> filter;

    public SlotStackFilter(Predicate<ItemStack> filter, IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        this.filter = filter;
        setStackLimit(64);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return filter.test(stack);
    }
}
