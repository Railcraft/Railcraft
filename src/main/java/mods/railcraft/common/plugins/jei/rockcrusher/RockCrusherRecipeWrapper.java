/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rockcrusher;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RockCrusherRecipeWrapper implements IRecipeWrapper {
    private IRockCrusherCrafter.IRecipe recipe;

    public RockCrusherRecipeWrapper(IRockCrusherCrafter.IRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getInput().getMatchingStacks()));
        ingredients.setOutputs(VanillaTypes.ITEM, transform());
    }

    public List<ItemStack> transform() {
        List<ItemStack> lists = new ArrayList<>();
        for (IOutputEntry entry : recipe.getOutputs()) {
            lists.add(entry.getOutput());
        }
        return lists;
    }

    public IRockCrusherCrafter.IRecipe getRecipe() {
        return recipe;
    }
}
