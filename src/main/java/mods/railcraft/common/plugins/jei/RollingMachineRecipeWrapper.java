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
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapedRecipesWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessOreRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipesWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RollingMachineRecipeWrapper extends BlankRecipeWrapper {
    private final IRecipe recipe;
    private final IRecipeWrapper wrapper;
    private final boolean shaped;

    public RollingMachineRecipeWrapper(IJeiHelpers jeiHelpers, IRecipe recipe) {
        this.recipe = recipe;
        if (recipe instanceof ShapedRecipes) {
            wrapper = new ShapedRecipesWrapper((ShapedRecipes) recipe);
            shaped = true;
        } else if (recipe instanceof ShapelessRecipes) {
            wrapper = new ShapelessRecipesWrapper(jeiHelpers.getGuiHelper(), (ShapelessRecipes) recipe);
            shaped = false;
        } else if (recipe instanceof ShapedOreRecipe) {
            wrapper = new ShapedOreRecipeWrapper(jeiHelpers, (ShapedOreRecipe) recipe);
            shaped = true;
        } else if (recipe instanceof ShapelessOreRecipe) {
            wrapper = new ShapelessOreRecipeWrapper(jeiHelpers, (ShapelessOreRecipe) recipe);
            shaped = false;
        } else {
            wrapper = null;
            shaped = false;
        }
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    public int getWidth() {
        if (wrapper instanceof IShapedCraftingRecipeWrapper)
            return ((IShapedCraftingRecipeWrapper) wrapper).getWidth();
        return 1;
    }

    public int getHeight() {
        if (wrapper instanceof IShapedCraftingRecipeWrapper)
            return ((IShapedCraftingRecipeWrapper) wrapper).getHeight();
        return 1;
    }

    public boolean isShaped() {
        return shaped;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (wrapper != null)
            wrapper.getIngredients(ingredients);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (wrapper != null)
            wrapper.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
    }
}
