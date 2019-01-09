/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.blastfurnace;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.RailcraftJEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class BlastFurnaceMachineCategory implements IRecipeCategory<BlastFurnaceRecipeProvider.BFWrapper> {

    public static final int WIDTH = 82;
    public static final int HEIGHT = 54;

    private final IDrawable background;
    private final String localizedName;
    private final IDrawable progress;
    private final IDrawable flame;

    public BlastFurnaceMachineCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_blast_furnace.png");
        background = guiHelper.createDrawable(location, 55, 16, WIDTH, HEIGHT);
        localizedName = LocalizationPlugin.translate("gui.railcraft.jei.category.blast_furnace");
        this.progress = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_blast_furnace.png"), 177, 14, 22, 15), 200, IDrawableAnimated.StartDirection.LEFT, false);
        this.flame = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_blast_furnace.png"), 176, 0, 14, 14), 200, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public String getModName() {
        return Railcraft.MOD_ID;
    }

    @Override
    public String getUid() {
        return RailcraftJEIPlugin.BLAST_FURNACE;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        progress.draw(minecraft, 25, 19);
        flame.draw(minecraft, 2, 21);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlastFurnaceRecipeProvider.BFWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 0, 0);
        recipeLayout.getItemStacks().init(1, false, 60, 4);
        recipeLayout.getItemStacks().init(2, false, 60, 36);
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(1));
    }
}
