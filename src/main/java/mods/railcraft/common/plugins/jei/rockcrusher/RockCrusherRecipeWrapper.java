package mods.railcraft.common.plugins.jei.rockcrusher;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.ICrusherRecipe;
import mods.railcraft.api.crafting.IOutputEntry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RockCrusherRecipeWrapper implements IRecipeWrapper {
    private ICrusherRecipe recipe;

    public RockCrusherRecipeWrapper(ICrusherRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getInput().getMatchingStacks()));
        ingredients.setOutputLists(VanillaTypes.ITEM, transform());
    }

    public List<List<ItemStack>> transform() {
        List<List<ItemStack>> lists = new ArrayList<>();
        for (IOutputEntry entry : recipe.getOutputs()) {
            lists.add(Collections.singletonList(entry.getOutput()));
        }
        return lists;
    }
}
