/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Predicate;

/**
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
        return containsKey(new ItemKey(item, meta)) || containsKey(new ItemKey(item));
    }

    public boolean containsKey(@Nullable ItemStack stack) {
        return stack != null && containsKey(stack.getItem(), stack.getItemDamage());
    }

    public Predicate<ItemStack> getStackFilter() {
        return stack -> containsKey(stack);
    }
}
