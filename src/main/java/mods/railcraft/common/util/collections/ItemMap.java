/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import java.util.HashMap;

import mods.railcraft.api.core.items.IStackFilter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @param <V>
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemMap<V> extends HashMap<ItemKey, V> {
    public V put(Item item, int meta, V value) {
        return put(new ItemKey(item, meta), value);
    }

    public V put(Item item, V value) {
        return put(new ItemKey(item), value);
    }

    public V get(Item item) {
        return get(new ItemKey(item));
    }

    public V get(Item item, int meta) {
        V value = get(new ItemKey(item, meta));
        if (value != null)
            return value;
        return get(new ItemKey(item));
    }

    public V get(ItemStack stack) {
        return get(stack.getItem(), stack.getItemDamage());
    }

    public boolean containsKey(Item item, int meta) {
        if (containsKey(new ItemKey(item, meta)))
            return true;
        return containsKey(new ItemKey(item));
    }

    public boolean containsKey(ItemStack stack) {
        if (stack == null)
            return false;
        return containsKey(stack.getItem(), stack.getItemDamage());
    }

    public IStackFilter getStackFilter() {
        return new IStackFilter() {
            @Override
            public boolean matches(ItemStack stack) {
                return containsKey(stack);
            }
        };
    }
}
