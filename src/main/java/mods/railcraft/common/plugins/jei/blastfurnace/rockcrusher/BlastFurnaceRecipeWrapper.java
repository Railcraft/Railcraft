package mods.railcraft.common.plugins.jei.blastfurnace.rockcrusher;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;

import java.util.Collections;

public class BlastFurnaceRecipeWrapper implements IRecipeWrapper {
    private IBlastFurnaceRecipe recipe;

    public BlastFurnaceRecipeWrapper(IBlastFurnaceRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getInput().getMatchingStacks()));
        ingredients.setOutputLists(VanillaTypes.ITEM, Lists.newArrayList(Collections.singletonList(recipe.getOutput()), Collections.singletonList(recipe.getSecondOutput())));
    }
}
