package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mods.railcraft.common.util.crafting.ShapedRollingMachineRecipe;
import mods.railcraft.common.util.crafting.ShapelessRollingMachineRecipe;

/**
 *
 */
public final class ShapelessRollingMachineRecipeWrapper extends RollingMachineRecipeWrapper<ShapelessRollingMachineRecipe> {

    private IStackHelper helper;

    public ShapelessRollingMachineRecipeWrapper(ShapelessRollingMachineRecipe recipe, IStackHelper helper) {
        super(recipe);
        this.helper = helper;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getSampleOutput());
        ingredients.setInputLists(VanillaTypes.ITEM, helper.expandRecipeItemStackInputs(recipe.getIngredients()));
    }
}
