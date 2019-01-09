/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.plugins.jei.RecipeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

public class CokeOvenRecipeProvider extends RecipeProvider<ICokeOvenCrafter.IRecipe> {

    public static CokeOvenRecipeProvider get(IModRegistry registry) {
        return new CokeOvenRecipeProvider(registry);
    }

    private CokeOvenRecipeProvider(IModRegistry registry) {
        super(registry);
    }

    @Override
    protected List<ICokeOvenCrafter.IRecipe> getRawRecipes() {
        return Crafters.cokeOven().getRecipes();
    }

    @Override
    protected COWrapper wrap(ICokeOvenCrafter.IRecipe recipe) {
        return new COWrapper(recipe);
    }

    public final class COWrapper implements IRecipeWrapper {
        private final ICokeOvenCrafter.IRecipe recipe;

        public COWrapper(ICokeOvenCrafter.IRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(helpers.getStackHelper().toItemStackList(recipe.getInput())));
            ItemStack output = recipe.getOutput();
            if (!output.isEmpty()) {
                ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
            }
            FluidStack outputFluid = recipe.getFluidOutput();
            if (outputFluid != null) {
                ingredients.setOutput(VanillaTypes.FLUID, outputFluid);
            }
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            drawTickTime(recipe, minecraft, 30, 43, false);
        }
    }
}
