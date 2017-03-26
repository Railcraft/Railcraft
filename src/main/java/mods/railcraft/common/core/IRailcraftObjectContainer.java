/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This interface is mainly to ensure that RailcraftBlocks and RailcraftItems have similar syntax.
 *
 * Created by CovertJaguar on 4/13/2016.
 */
public interface IRailcraftObjectContainer<T extends IRailcraftObject<?>> extends IRailcraftRecipeIngredient {
    default void register() {
    }

    boolean isEqual(ItemStack stack);

    String getBaseTag();

    @Nullable
    default ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    @Nullable
    default ItemStack getStack() {
        return getStack(1);
    }

    @Nullable
    default ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    @Nullable
    default ItemStack getStack(int qty, int meta) {
        return getObject().map(o -> o.getStack(qty, meta)).orElse(null);
    }

    @Nullable
    default ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nullable
    default ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return getObject().map(o -> o.getStack(qty, variant)).orElse(null);
    }

    Optional<T> getObject();

    @Override
    @Nullable
    default Object getRecipeObject() {
        return getRecipeObject(null);
    }

    @Override
    @Nullable
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        Object obj = getObject().map(o -> {
            o.checkVariant(variant);
            return o.getRecipeObject(variant);
        }).orElse(null);
        if (obj == null && variant != null)
            obj = variant.getAlternate(this);
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    boolean isEnabled();

    boolean isLoaded();
}
