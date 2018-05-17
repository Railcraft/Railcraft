package mods.railcraft.common.plugins.jei.crafting;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import mods.railcraft.common.util.crafting.ShapedFluidRecipe;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class ShapedFluidRecipeWrapper implements IShapedCraftingRecipeWrapper {
    private final ShapedFluidRecipe recipe;
    private final int width;
    private final int height;

    public ShapedFluidRecipeWrapper(ShapedFluidRecipe recipe) {
        this.recipe = recipe;
        for (Object input : this.recipe.getInput()) {
            if (input instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) input;
                if (itemStack.getCount() != 1) {
                    itemStack.setCount(1);
                }
            }
        }
        this.width = recipe.getWidth();
        this.height = recipe.getHeight();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack recipeOutput = recipe.getRecipeOutput();

        try {
            List<List<ItemStack>> inputs = FluidRecipeInterpreter.expand(Arrays.asList(recipe.getInput()));
            ingredients.setInputLists(ItemStack.class, inputs);
            if (!recipeOutput.isEmpty()) {
                ingredients.setOutput(ItemStack.class, recipeOutput);
            }
        } catch (RuntimeException e) {
            String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(recipe, Arrays.asList(recipe.getInput()), recipeOutput);
            throw new BrokenCraftingRecipeException(info, e);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
