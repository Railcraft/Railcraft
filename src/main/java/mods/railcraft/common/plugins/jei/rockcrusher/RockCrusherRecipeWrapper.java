/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rockcrusher;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RockCrusherRecipeWrapper implements IRecipeWrapper {
    private final IJeiHelpers helpers;
    private IRockCrusherCrafter.IRecipe recipe;

    public RockCrusherRecipeWrapper(IJeiHelpers helpers, IRockCrusherCrafter.IRecipe recipe) {
        this.helpers = helpers;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(helpers.getStackHelper().toItemStackList(recipe.getInput())));
        ingredients.setOutputs(VanillaTypes.ITEM, transform());
    }

    public List<ItemStack> transform() {
        return recipe.getOutputs().stream().map(IOutputEntry::getOutput).collect(Collectors.toList());
    }

    public IRockCrusherCrafter.IRecipe getRecipe() {
        return recipe;
    }
}
