package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mods.railcraft.common.util.crafting.ShapedRollingMachineRecipe;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ShapedRollingMachineRecipeWrapper extends RollingMachineRecipeWrapper<ShapedRollingMachineRecipe> {

    private IStackHelper helper;

    public ShapedRollingMachineRecipeWrapper(ShapedRollingMachineRecipe recipe, IStackHelper helper) {
        super(recipe);
        this.helper = helper;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getSampleOutput());
        ingredients.setInputLists(VanillaTypes.ITEM, helper.expandRecipeItemStackInputs(fixIngredients()));
    }

    private List<Ingredient> fixIngredients() {
        List<Ingredient> initial = recipe.getIngredients();
        List<Ingredient> result = new ArrayList<>();
        int width = recipe.getWidth();
        int counter = 0;
        for (Ingredient ingredient : initial) {
            result.add(ingredient);
            counter++;
            if (counter == width) {
                for (int i = 3 - width; i > 0; i--) {
                    result.add(Ingredient.EMPTY);
                }
            }
        }
        return result;
    }
}
