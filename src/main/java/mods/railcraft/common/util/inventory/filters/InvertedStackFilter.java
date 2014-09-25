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

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InvertedStackFilter implements IStackFilter {

    private final IStackFilter filter;

    public InvertedStackFilter(IStackFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return !filter.matches(stack);
    }
}
