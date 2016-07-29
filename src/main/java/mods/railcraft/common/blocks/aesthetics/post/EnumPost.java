/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumPost implements IVariantEnumBlock {

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

    @Nullable
    public ItemStack getStack() {
        return getStack(1);
    }

    @Nullable
    public ItemStack getStack(int qty) {
        Block block = getBlock();
        if (!isEnabled() || block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    public boolean isEnabled() {
        return RailcraftConfig.isSubBlockEnabled(getTag());
    }

    public boolean canBurn() {
        return this == WOOD || this == WOOD_PLATFORM;
    }

    public String getTag() {
        return "tile.railcraft.post." + getBaseTag();
    }

    public String getBaseTag() {
        return name().toLowerCase(Locale.ROOT).replace("_", ".");
    }

    @Nonnull
    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftObjectContainer container) {
        return null;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.post;
    }

}
