/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.blastfurnace;

import com.google.common.collect.Lists;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.jei.RecipeProvider;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.List;

public final class BlastFurnaceRecipeProvider extends RecipeProvider<IBlastFurnaceCrafter.IRecipe> {

    public static BlastFurnaceRecipeProvider get(IModRegistry registry) {
        return new BlastFurnaceRecipeProvider(registry);
    }

    private BlastFurnaceRecipeProvider(IModRegistry registry) {
        super(registry);
    }

    @Override
    protected List<IBlastFurnaceCrafter.IRecipe> getRawRecipes() {
        return Crafters.blastFurnace().getRecipes();
    }

    @Override
    protected BFWrapper wrap(IBlastFurnaceCrafter.IRecipe recipe) {
        return new BFWrapper(recipe);
    }

    public class BFWrapper implements IRecipeWrapper {
        private IBlastFurnaceCrafter.IRecipe recipe;

        public BFWrapper(IBlastFurnaceCrafter.IRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(helpers.getStackHelper().toItemStackList(recipe.getInput())));
            ingredients.setOutputLists(VanillaTypes.ITEM, Lists.newArrayList(Collections.singletonList(recipe.getOutput()),
                    Collections.singletonList(RailcraftItems.DUST.getStack(recipe.getSlagOutput(), ItemDust.EnumDust.SLAG))));
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            drawTickTime(recipe, minecraft, 38, 38, true);
        }
    }
}
