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
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ArrayStackFilter extends StackFilter {

    private final ItemStack[] stacks;

    public ArrayStackFilter(ItemStack... stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean apply(final ItemStack stack) {
        if (stacks.length == 0 || !hasFilter()) {
            return true;
        }
        return InvTools.isItemEqual(stack, stacks);
    }

    public ItemStack[] getStacks() {
        return stacks;
    }

    public boolean hasFilter() {
        for (ItemStack filter : stacks) {
            if (filter != null) {
                return true;
            }
        }
        return false;
    }
}
