/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeHandler;
import mezz.jei.plugins.vanilla.crafting.ShapedRecipesHandler;
import mezz.jei.plugins.vanilla.crafting.ShapelessOreRecipeHandler;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipesHandler;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RollingMachineRecipeHandler implements IRecipeHandler<RollingMachineRecipeWrapper> {
    private final IJeiHelpers jeiHelpers;

    public RollingMachineRecipeHandler(IJeiHelpers jeiHelpers) {
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public Class<RollingMachineRecipeWrapper> getRecipeClass() {
        return RollingMachineRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return RollingMachineRecipeCategory.CATEGORY_UID;
    }

    @Override
    public String getRecipeCategoryUid(RollingMachineRecipeWrapper recipe) {
        return RollingMachineRecipeCategory.CATEGORY_UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(RollingMachineRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(RollingMachineRecipeWrapper recipe) {
        if (recipe.getRecipe() instanceof ShapedRecipes) {
            return new ShapedRecipesHandler().isRecipeValid((ShapedRecipes) recipe.getRecipe());
        } else if (recipe.getRecipe() instanceof ShapelessRecipes) {
            return new ShapelessRecipesHandler(jeiHelpers.getGuiHelper()).isRecipeValid((ShapelessRecipes) recipe.getRecipe());
        } else if (recipe.getRecipe() instanceof ShapedOreRecipe) {
            return new ShapedOreRecipeHandler(jeiHelpers).isRecipeValid((ShapedOreRecipe) recipe.getRecipe());
        } else if (recipe.getRecipe() instanceof ShapelessOreRecipe) {
            return new ShapelessOreRecipeHandler(jeiHelpers).isRecipeValid((ShapelessOreRecipe) recipe.getRecipe());
        }
        return false;
    }
}
