/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/

package mods.railcraft.common.items;

/**
 * Created by CovertJaguar on 3/14/2016.
 */
public interface IRailcraftItem {
    Object getRecipeObject(IItemMetaEnum meta);

    void defineRecipes();

    void definePostRecipes();

    void initItem();
}
