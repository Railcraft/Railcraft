/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemKey {

    public final Item item;
    public final int metadata;

    public ItemKey(Item item) {
        this.item = item;
        this.metadata = -1;
    }

    public ItemKey(Item item, int metadata) {
        this.item = item;
        this.metadata = metadata;
    }

    public ItemStack asStack() {
        return new ItemStack(item, 1, metadata == -1 ? OreDictionary.WILDCARD_VALUE : metadata);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.item.hashCode();
        hash = 73 * hash + this.metadata;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ItemKey other = (ItemKey) obj;
        if (this.item != other.item) return false;
        return this.metadata == other.metadata;
    }

}
