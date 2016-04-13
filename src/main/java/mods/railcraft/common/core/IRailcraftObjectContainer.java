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

/**
 * This interface is mainly to ensure that RailcraftBlocks and RailcraftItems have similar syntax.
 *
 * Created by CovertJaguar on 4/13/2016.
 */
public interface IRailcraftObjectContainer {
    void register();

    boolean isEqual(ItemStack stack);

    String getBaseTag();

    ItemStack getWildcard();

    ItemStack getStack();

    ItemStack getStack(int qty);

    ItemStack getStack(int qty, int meta);

    ItemStack getStack(IVariantEnum variant);

    ItemStack getStack(int qty, IVariantEnum variant);

    IRailcraftObject getObject();

    Object getRecipeObject();

    Object getRecipeObject(IVariantEnum variant);

    boolean isEnabled();

    boolean isLoaded();
}
