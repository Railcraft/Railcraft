/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * This interface is mainly to ensure that RailcraftBlocks and RailcraftItems have similar syntax.
 *
 * Created by CovertJaguar on 4/13/2016.
 */
public interface IRailcraftObjectContainer {
    void register();

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
    ItemStack getStack(int qty, int meta);

    @Nullable
    default ItemStack getStack(IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nullable
    ItemStack getStack(int qty, IVariantEnum variant);

    IRailcraftObject getObject();

    @Nullable
    default Object getRecipeObject() {
        return getRecipeObject(null);
    }

    @Nullable
    Object getRecipeObject(@Nullable IVariantEnum variant);

    boolean isEnabled();

    boolean isLoaded();
}
