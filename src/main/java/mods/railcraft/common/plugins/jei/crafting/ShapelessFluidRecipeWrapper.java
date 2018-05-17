package mods.railcraft.common.plugins.jei.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import mods.railcraft.common.util.crafting.ShapelessFluidRecipe;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class ShapelessFluidRecipeWrapper implements ICraftingRecipeWrapper {
    private final ShapelessFluidRecipe recipe;

    public ShapelessFluidRecipeWrapper(ShapelessFluidRecipe recipe) {
        this.recipe = recipe;
        for (Object input : this.recipe.getInput()) {
            if (input instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) input;
                if (itemStack.getCount() != 1) {
                    itemStack.setCount(1);
                }
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack recipeOutput = recipe.getRecipeOutput();
        try {
            List<List<ItemStack>> inputs = FluidRecipeInterpreter.expand(recipe.getInput());
            ingredients.setInputLists(ItemStack.class, inputs);
            if (!recipeOutput.isEmpty()) {
                ingredients.setOutput(ItemStack.class, recipeOutput);
            }
        } catch (RuntimeException e) {
            String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(recipe, recipe.getInput(), recipeOutput);
            throw new BrokenCraftingRecipeException(info, e);
        }
    }
}
