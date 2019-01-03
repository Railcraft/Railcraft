/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.RailcraftJEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RollingMachineRecipeCategory implements IRecipeCategory<IRecipeWrapper> {

    private static final int CRAFT_OUTPUT_SLOT = 0;
    private static final int CRAFT_INPUT_SLOT1 = 1;

    public static final int WIDTH = 116;
    public static final int HEIGHT = 54;

    private final IDrawable background;
    private final String localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public RollingMachineRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
        background = guiHelper.createDrawable(location, 29, 16, WIDTH, HEIGHT);
        localizedName = LocalizationPlugin.translate("gui.railcraft.jei.category.rolling");
        craftingGridHelper = guiHelper.createCraftingGridHelper(CRAFT_INPUT_SLOT1, CRAFT_OUTPUT_SLOT);
    }

    @Override
    public String getModName() {
        return Railcraft.MOD_ID;
    }

    @Override
    public String getUid() {
        return RailcraftJEIPlugin.ROLLING;
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
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiItemStacks.init(CRAFT_OUTPUT_SLOT, false, 94, 18);
        guiItemStacks.set(CRAFT_OUTPUT_SLOT, outputs.get(0));


        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = CRAFT_INPUT_SLOT1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        craftingGridHelper.setInputs(guiItemStacks, inputs);

    }

}
