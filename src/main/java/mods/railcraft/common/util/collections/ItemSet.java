/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.api.core.items.StackFilter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemSet extends HashSet<ItemKey> {
    public boolean add(Item item, int meta) {
        return add(new ItemKey(item, meta));
    }

    public boolean add(Item item) {
        return add(new ItemKey(item));
    }

    public boolean contains(Item item, int meta) {
        if (contains(new ItemKey(item)))
            return true;
        return contains(new ItemKey(item, meta));
    }

    public boolean contains(ItemStack stack) {
        if (stack == null)
            return false;
        return contains(stack.getItem(), stack.getItemDamage());
    }

    public IStackFilter getStackFilter() {
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return contains(stack);
            }
        };
    }
}
