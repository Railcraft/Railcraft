/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rockcrusher;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerRockCrusher;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.RailcraftJEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class RockCrusherMachineCategory implements IRecipeCategory<RockCrusherRecipeProvider.RCWrapper> {

    public static final int WIDTH = 144;
    public static final int HEIGHT = 54;

    private final IDrawable background;
    private final String localizedName;
    private final IDrawable progress;

    public RockCrusherMachineCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_crusher.png");
        background = guiHelper.createDrawable(location, 0, ContainerRockCrusher.GUI_HEIGHT, WIDTH, HEIGHT);
        localizedName = LocalizationPlugin.translate("gui.railcraft.jei.category.crushing");
        this.progress = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_crusher.png"), 144, ContainerRockCrusher.GUI_HEIGHT, 29, 53), 500, IDrawableAnimated.StartDirection.LEFT, false);

    }

    @Override
    public String getModName() {
        return Railcraft.MOD_ID;
    }

    @Override
    public String getUid() {
        return RailcraftJEIPlugin.ROCK_CRUSHER;
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
        progress.draw(minecraft, 58, 0);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RockCrusherRecipeProvider.RCWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 18, 18);
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 0)
                return;
            if (recipeWrapper.getRecipe().getOutputs().size() >= slotIndex)
                tooltip.addAll(recipeWrapper.getRecipe().getOutputs().get(slotIndex - 1).getGenRule().getToolTip().stream().map(ITextComponent::getFormattedText).collect(Collectors.toSet()));
        });
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = 1 + x + (y * 3);
                recipeLayout.getItemStacks().init(index, true, 90 + x * 18, y * 18);
                if (outputs.size() > index - 1)
                    recipeLayout.getItemStacks().set(index, outputs.get(index - 1));
                else
                    recipeLayout.getItemStacks().set(index, ItemStack.EMPTY);
            }
        }
    }
}
