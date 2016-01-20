/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import net.minecraft.item.ItemStack;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumPost {

    WOOD,
    STONE,
    METAL_UNPAINTED,
    EMBLEM,
    WOOD_PLATFORM,
    STONE_PLATFORM,
    METAL_PLATFORM_UNPAINTED;
    public static final EnumPost[] VALUES = values();

    public static EnumPost fromId(int id) {
        if (id < 0 || id >= EnumPost.values().length)
            id = 0;
        return EnumPost.values()[id];
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        if (!isEnabled())
            return null;
        return new ItemStack(BlockPost.block, qty, ordinal());
    }

    public boolean isEnabled() {
        return BlockPost.block != null;
    }

    public String getTag() {
        return "tile.railcraft.post." + name().toLowerCase(Locale.ENGLISH).replace("_", ".");
    }

}
