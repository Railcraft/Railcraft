package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

/**
 *
 */
public final class CokeOvenWrapper implements IRecipeWrapper {

    private static final ResourceLocation COKE_OVEN_BACKGROUND = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_coke_oven.png");
    private final IJeiHelpers helpers;
    private final ICokeOvenRecipe recipe;
    private final IDrawableStatic flame;
    private final IDrawableStatic arrow;

    public CokeOvenWrapper(IJeiHelpers helpers, ICokeOvenRecipe recipe) {
        this.helpers = helpers;
        this.recipe = recipe;
        this.flame = helpers.getGuiHelper().createDrawable(COKE_OVEN_BACKGROUND, 176, 47, 14, 14);
        this.arrow = helpers.getGuiHelper().createDrawable(COKE_OVEN_BACKGROUND, 176, 61, 22, 16);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, helpers.getStackHelper().toItemStackList(recipe.getInput()));
        ItemStack output = recipe.getOutput();
        if (!output.isEmpty()) {
            ingredients.setOutput(ItemStack.class, recipe.getOutput());
        }
        FluidStack outputFluid = recipe.getFluidOutput();
        if (outputFluid != null) {
            ingredients.setOutput(FluidStack.class, outputFluid);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int cookTime = recipe.getCookTime();
        IDrawableAnimated animatedFire = helpers.getGuiHelper().createAnimatedDrawable(flame, cookTime, IDrawableAnimated.StartDirection.TOP, true);
        IDrawableAnimated animatedArrow = helpers.getGuiHelper().createAnimatedDrawable(arrow, cookTime, IDrawableAnimated.StartDirection.LEFT, false);
        animatedArrow.draw(minecraft);
        animatedFire.draw(minecraft);
        //TODO I18n
        minecraft.fontRenderer.drawString("Burn time: " + cookTime, mouseX, mouseY, Color.GRAY.getRGB());
//        minecraft.fontRenderer.
    }
}
