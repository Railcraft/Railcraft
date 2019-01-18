/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.collections;

import com.google.common.collect.ForwardingMap;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemMap<V> extends ForwardingMap<ItemKey, V> {
    @SuppressWarnings("rawtypes")
    private static ItemMap EMPTY = new ItemMap() {
        @Override
        protected Map delegate() {
            return Collections.emptyMap();
        }
    };

    @SuppressWarnings("unchecked")
    public static <V> ItemMap<V> emptyMap() {
        return (ItemMap<V>) EMPTY;
    }

    private Map<ItemKey, V> delegate = new HashMap<>();

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

    @Override
    protected Map<ItemKey, V> delegate() {
        return delegate;
    }

    public V get(ItemStack stack) {
        return get(stack.getItem(), stack.getItemDamage());
    }

    public boolean containsKey(Item item, int meta) {
        return containsKey(new ItemKey(item, meta)) || containsKey(new ItemKey(item));
    }

    public boolean containsKey(@Nullable ItemStack stack) {
        return !InvTools.isEmpty(stack) && containsKey(stack.getItem(), stack.getItemDamage());
    }

    public Predicate<ItemStack> getStackFilter() {
        return this::containsKey;
    }
}
