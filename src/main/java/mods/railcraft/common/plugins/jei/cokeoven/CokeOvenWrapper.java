/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.Collections;

public final class CokeOvenWrapper implements IRecipeWrapper {
    private final IJeiHelpers helpers;
    private final ICokeOvenCrafter.IRecipe recipe;

    public CokeOvenWrapper(IJeiHelpers helpers, ICokeOvenCrafter.IRecipe recipe) {
        this.helpers = helpers;
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
        ItemStack input = helpers.getStackHelper().toItemStackList(recipe.getInput()).get(0);
        if (InvTools.nonEmpty(input)) {
            int cookTime = recipe.getTickTime(input);
            minecraft.fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.jei.burntime", cookTime), -2, 41, Color.GRAY.getRGB());
        }
    }
}
