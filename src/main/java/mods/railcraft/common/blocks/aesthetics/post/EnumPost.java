/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumPost implements IStringSerializable {

    WOOD(MapColor.brownColor),
    STONE(MapColor.stoneColor),
    METAL_UNPAINTED(MapColor.netherrackColor),
    EMBLEM(MapColor.ironColor),
    WOOD_PLATFORM(MapColor.brownColor),
    STONE_PLATFORM(MapColor.stoneColor),
    METAL_PLATFORM_UNPAINTED(MapColor.netherrackColor);
    public static final EnumPost[] VALUES = values();
    private final MapColor mapColor;

    EnumPost(MapColor mapColor) {
        this.mapColor = mapColor;
    }

    public static EnumPost fromId(int id) {
        if (id < 0 || id >= EnumPost.values().length)
            id = 0;
        return EnumPost.values()[id];
    }

    public final MapColor getMapColor() {
        return mapColor;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int qty) {
        if (!isEnabled())
            return null;
        return new ItemStack(BlockPost.getBlock(), qty, ordinal());
    }

    public boolean isEnabled() {
        return BlockPost.getBlock() != null;
    }

    public boolean canBurn() {
        return this == WOOD || this == WOOD_PLATFORM;
    }

    public String getTag() {
        return "tile.railcraft.post." + getName();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH).replace("_", ".");
    }
}
