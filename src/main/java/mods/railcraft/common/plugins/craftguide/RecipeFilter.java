/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.craftguide;

import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import uristqwerty.CraftGuide.api.BasicRecipeFilter;
import uristqwerty.CraftGuide.api.CraftGuideRecipe;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RecipeFilter implements BasicRecipeFilter
{

    ItemStack stackTankCart = RailcraftRegistry.getItem("cart.tank", 1);
    ItemStack stackCart = new ItemStack(Items.MINECART);

    @Override
    public boolean shouldKeepRecipe(CraftGuideRecipe recipe, ItemStack recipeType) {
        boolean tankCart = recipe.containsItem(stackTankCart);
        boolean cart = recipe.containsItem(stackCart);

        return !(tankCart && !cart);
    }
}
