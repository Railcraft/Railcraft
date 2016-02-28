/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import mods.railcraft.api.core.items.StackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryStackFilter extends StackFilter {

    private final IInventory inv;

    public InventoryStackFilter(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public boolean apply(final ItemStack stack) {
        return InvTools.containsItem(inv, stack);
    }
}
