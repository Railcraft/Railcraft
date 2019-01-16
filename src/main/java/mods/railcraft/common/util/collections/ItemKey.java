/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.collections;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class ItemKey {

    public final IRegistryDelegate<Item> item;
    public final int metadata;

    public ItemKey(Item item) {
        this.item = item.delegate;
        this.metadata = -1;
    }

    public ItemKey(Item item, int metadata) {
        this.item = item.delegate;
        this.metadata = metadata;
    }

    public ItemKey(ItemStack stack) {
        this(stack.getItem(), stack.getMetadata());
    }

    public ItemStack asStack() {
        return new ItemStack(item.get(), 1, metadata == -1 ? OreDictionary.WILDCARD_VALUE : metadata);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + item.hashCode();
        hash = 73 * hash + metadata;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ItemKey other = (ItemKey) obj;
        if (item != other.item) return false;
        return metadata == other.metadata;
    }

    @Override
    public String toString() {
        String s = item.name().toString();
        if (metadata != -1)
            s += "#" + metadata;
        return s;
    }
}
