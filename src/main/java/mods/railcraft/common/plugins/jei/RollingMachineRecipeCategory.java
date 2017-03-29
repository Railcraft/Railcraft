/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RollingMachineRecipeCategory extends BlankRecipeCategory<RollingMachineRecipeWrapper> {
    public static final String CATEGORY_UID = "railcraft.rolling";

    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    public static final int width = 116;
    public static final int height = 54;

    private final IDrawable background;
    private final String localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public RollingMachineRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
        background = guiHelper.createDrawable(location, 29, 16, width, height);
        localizedName = LocalizationPlugin.translate("gui.railcraft.jei.category.rollingMachine");
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
    }

    @Override
    public String getUid() {
        return CATEGORY_UID;
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
    public void setRecipe(IRecipeLayout recipeLayout, RollingMachineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(craftOutputSlot, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

        if (recipeWrapper.isShaped()) {
            craftingGridHelper.setInputStacks(guiItemStacks, inputs, recipeWrapper.getWidth(), recipeWrapper.getHeight());
            craftingGridHelper.setOutput(guiItemStacks, outputs);
        } else {
            craftingGridHelper.setInputStacks(guiItemStacks, inputs);
            craftingGridHelper.setOutput(guiItemStacks, outputs);
        }
    }

}
