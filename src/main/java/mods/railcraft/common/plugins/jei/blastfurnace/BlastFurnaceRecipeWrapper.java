/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.blastfurnace;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.client.Minecraft;

import java.util.Collections;

public class BlastFurnaceRecipeWrapper implements IRecipeWrapper {
    private IBlastFurnaceCrafter.IRecipe recipe;

    public BlastFurnaceRecipeWrapper(IBlastFurnaceCrafter.IRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getInput().getMatchingStacks()));
        ingredients.setOutputLists(VanillaTypes.ITEM, Lists.newArrayList(Collections.singletonList(recipe.getOutput()),
                Collections.singletonList(RailcraftItems.DUST.getStack(recipe.getSlagOutput(), ItemDust.EnumDust.SLAG))));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int cookTime = recipe.getCookTime();
        //TODO correct position
        //minecraft.fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.jei.burntime", cookTime), -2, 41, Color.GRAY.getRGB());
    }
}
