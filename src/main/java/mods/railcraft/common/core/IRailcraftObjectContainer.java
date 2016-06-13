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
    ItemStack getWildcard();

    @Nullable
    ItemStack getStack();

    @Nullable
    ItemStack getStack(int qty);

    @Nullable
    ItemStack getStack(int qty, int meta);

    @Nullable
    ItemStack getStack(IVariantEnum variant);

    @Nullable
    ItemStack getStack(int qty, IVariantEnum variant);

    IRailcraftObject getObject();

    @Nullable
    Object getRecipeObject();

    @Nullable
    Object getRecipeObject(IVariantEnum variant);

    boolean isEnabled();

    boolean isLoaded();
}
