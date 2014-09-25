/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;

/**
 * This is a custom data structure designed specifically
 * for using ItemStacks as keys in a Map.
 *
 * The keys index on the itemID and damage of the ItemStacks,
 * ignoring any other differences (NBT Tags, stackSize, etc...).
 *
 * @author CovertJaguar <http://www.railcraft.info>
 * @see Map
 * @param <V>
 */
public class ItemStackMap<V> implements Map<ItemStack, V>
{

    private class EntryWrapper implements Map.Entry<ItemStack, V>
    {

        private final Map.Entry<KeyWrapper, V> entry;

        public EntryWrapper(Map.Entry<KeyWrapper, V> e)
        {
            entry = e;
        }

        @Override
        public ItemStack getKey()
        {
            return entry.getKey().getStack();
        }

        @Override
        public V getValue()
        {
            return entry.getValue();
        }

        @Override
        public V setValue(V value)
        {
            return entry.setValue(value);
        }

        @Override
        public String toString()
        {
            return getKey().getItem().getUnlocalizedName() + "=" + getValue().toString();
        }
    }

    private static class KeyWrapper
    {

        private final ItemStack stack;

        public KeyWrapper(ItemStack stack)
        {
            this.stack = stack.copy();
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final KeyWrapper other = (KeyWrapper)obj;
            if(stack.getItem() != other.stack.getItem()) {
                return false;
            }
            if(stack.getItemDamage() != other.stack.getItemDamage()) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 23 * hash + stack.getItem().hashCode();
            hash = 23 * hash + stack.getItemDamage();
            return hash;
        }

        public ItemStack getStack()
        {
            return stack.copy();
        }
    }
    private final Map<KeyWrapper, V> map = new HashMap<KeyWrapper, V>();

    @Override
    public V put(ItemStack stack, V value)
    {
        return map.put(new KeyWrapper(stack), value);
    }

    @Override
    public Collection<V> values()
    {
        return map.values();
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public V remove(Object key)
    {
        if(key instanceof ItemStack) {
            return map.remove(new KeyWrapper((ItemStack)key));
        }
        return null;
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

    @Override
    public V get(Object key)
    {
        if(key instanceof ItemStack) {
            return map.get(new KeyWrapper((ItemStack)key));
        }
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ItemStackMap) {
            return map.equals(((ItemStackMap)o).map);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key)
    {
        if(key instanceof ItemStack) {
            return map.containsKey(new KeyWrapper((ItemStack)key));
        }
        return false;
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public void putAll(Map<? extends ItemStack, ? extends V> m)
    {
        for(Map.Entry<? extends ItemStack, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Set<ItemStack> keySet()
    {
        Set<ItemStack> keySet = new HashSet<ItemStack>();
        for(KeyWrapper w : map.keySet()) {
            keySet.add(w.getStack());
        }
        return keySet;
    }

    @Override
    public Set<Map.Entry<ItemStack, V>> entrySet()
    {
        Set<Map.Entry<ItemStack, V>> entrySet = new HashSet<Map.Entry<ItemStack, V>>();

        for(Map.Entry<KeyWrapper, V> entry : map.entrySet()) {
            entrySet.add(new EntryWrapper(entry));
        }
        return entrySet;
    }
}
