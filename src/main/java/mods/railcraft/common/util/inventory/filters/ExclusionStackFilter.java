/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import net.minecraft.item.ItemStack;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.util.inventory.InvTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ExclusionStackFilter implements IStackFilter {

    private final ItemStack[] exclude;

    public ExclusionStackFilter(ItemStack... exclude) {
        this.exclude = exclude;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null)
            return false;
        return !InvTools.isItemEqual(stack, exclude);
    }

}
