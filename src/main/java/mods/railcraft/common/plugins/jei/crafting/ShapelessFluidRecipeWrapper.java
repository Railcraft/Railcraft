package mods.railcraft.common.plugins.jei.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.plugins.vanilla.crafting.AbstractShapelessRecipeWrapper;
import mezz.jei.util.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import mods.railcraft.common.util.crafting.ShapelessFluidRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ShapelessFluidRecipeWrapper extends AbstractShapelessRecipeWrapper {
    private final IJeiHelpers jeiHelpers;
    private final ShapelessFluidRecipe recipe;

    public ShapelessFluidRecipeWrapper(IJeiHelpers jeiHelpers, ShapelessFluidRecipe recipe) {
        super(jeiHelpers.getGuiHelper());
        this.jeiHelpers = jeiHelpers;
        this.recipe = recipe;
        for (Object input : this.recipe.getInput()) {
            if (input instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) input;
                if (itemStack.stackSize != 1) {
                    itemStack.stackSize = 1;
                }
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        ItemStack recipeOutput = recipe.getRecipeOutput();

        try {
            List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getInput());
            ingredients.setInputLists(ItemStack.class, inputs);

            if (recipeOutput != null) {
                ingredients.setOutput(ItemStack.class, recipeOutput);
            }
        } catch (RuntimeException e) {
            String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(recipe, recipe.getInput(), recipeOutput);
            throw new BrokenCraftingRecipeException(info, e);
        }
    }

    @Override
    public List getInputs() {
        return recipe.getInput();
    }

    @Override
    public List<ItemStack> getOutputs() {
        return Collections.singletonList(recipe.getRecipeOutput());
    }
}
