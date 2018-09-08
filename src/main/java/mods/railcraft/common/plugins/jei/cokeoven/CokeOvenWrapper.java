package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

public final class CokeOvenWrapper implements IRecipeWrapper {
    private final IJeiHelpers helpers;
    private final ICokeOvenRecipe recipe;

    public CokeOvenWrapper(IJeiHelpers helpers, ICokeOvenRecipe recipe) {
        this.helpers = helpers;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, helpers.getStackHelper().toItemStackList(recipe.getInput()));
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
        int cookTime = recipe.getCookTime();
        minecraft.fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.jei.burntime", cookTime), -2, 41, Color.GRAY.getRGB());
    }
}
