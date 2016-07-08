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

import mods.railcraft.common.blocks.IBlockVariantEnum;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumPost implements IBlockVariantEnum<EnumPost> {

    WOOD(MapColor.BROWN),
    STONE(MapColor.STONE),
    METAL_UNPAINTED(MapColor.NETHERRACK),
    EMBLEM(MapColor.IRON),
    WOOD_PLATFORM(MapColor.BROWN),
    STONE_PLATFORM(MapColor.STONE),
    METAL_PLATFORM_UNPAINTED(MapColor.NETHERRACK);
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

    @Override
    public boolean isEnabled() {
        return BlockPost.getBlock() != null;
    }

    public boolean canBurn() {
        return this == WOOD || this == WOOD_PLATFORM;
    }

    public String getTag() {
        return "tile.railcraft.post." + getName();
    }

    @Nonnull
    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH).replace("_", ".");
    }

    @Override
    public boolean isValid(Class<?> clazz) {
        return clazz ==  BlockPost.class;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftObjectContainer container) {
        return null;
    }

    @Nullable
    @Override
    public Block getBlock() {
        return BlockPost.getBlock();
    }

    @Nullable
    @Override
    public IBlockState getState() {
        if (getBlock() == null) return null;
        return getBlock().getDefaultState().withProperty(BlockPost.VARIANT, this);
    }
}
