/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.RailcraftJEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CokeOvenCategory implements IRecipeCategory<CokeOvenRecipeProvider.COWrapper> {
    private static final int INPUT_SLOTS = 0;
    private static final int OUTPUT_SLOTS = 1;
    private static final int OUTPUT_TANKS = 0;

    private final IDrawable background;
    private final IDrawable tankOverlay;
    private final IDrawable flame;
    private final IDrawable arrow;
    private final String title;

    private static final ResourceLocation COKE_OVEN_BACKGROUND = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_coke_oven.png");

    public CokeOvenCategory(IGuiHelper guiHelper) {
        title = LocalizationPlugin.translateFast("gui.railcraft.jei.category.coke");
        background = guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 15, 23, 124, 49);
        this.tankOverlay = guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 176, 0, 48, 47);

        this.flame = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 176, 47, 14, 14),200, IDrawableAnimated.StartDirection.TOP, true);
        this.arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 176, 61, 22, 15),200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public String getUid() {
        return RailcraftJEIPlugin.COKE;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return Railcraft.NAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        flame.draw(minecraft,2,4);
        arrow.draw(minecraft, 20, 21);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CokeOvenRecipeProvider.COWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiItemStacks.init(INPUT_SLOTS, true, 0, 19);
        guiItemStacks.init(OUTPUT_SLOTS, false, 45, 19);
        guiFluidStacks.init(OUTPUT_TANKS, false, 75, 1, 48, 47, 10_000, true, tankOverlay);
        guiItemStacks.set(INPUT_SLOTS, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(OUTPUT_SLOTS, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        guiFluidStacks.set(OUTPUT_TANKS, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}
