package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.jei.RailcraftJEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;

public class CokeOvenCategory implements IRecipeCategory<CokeOvenWrapper> {
    private static final int INPUT_SLOTS = 0;
    private static final int OUTPUT_SLOTS = 1;
    private static final int OUTPUT_TANKS = 0;

    private final IDrawable background;
    private final IDrawable tankOverlay;
    private final IDrawableAnimated progress;
    private final ICraftingGridHelper craftingGridHelper;
    private final String title;

    private static final ResourceLocation COKE_OVEN_BACKGROUND = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_coke_oven.png");

    public CokeOvenCategory(IGuiHelper guiHelper) {
        title = I18n.translateToLocal("railcraft.coke");
        background = guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 29, 16, 116, 54);
        this.tankOverlay = guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 196, 0, 12, 47);

        IDrawableStatic progressStatic = guiHelper.createDrawable(COKE_OVEN_BACKGROUND, 176, 14, 20, 18);
        progress = guiHelper.createAnimatedDrawable(progressStatic, 250, IDrawableAnimated.StartDirection.LEFT, false);

        craftingGridHelper = guiHelper.createCraftingGridHelper(INPUT_SLOTS, OUTPUT_SLOTS);
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
        progress.draw(minecraft, 62, 18);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CokeOvenWrapper recipeWrapper,
                          @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiItemStacks.init(INPUT_SLOTS, true, 18, 18);
        guiItemStacks.init(OUTPUT_SLOTS, false, 94, 18);
        guiFluidStacks.init(OUTPUT_TANKS, false, 94, 18, 12, 47, 10_000, true, tankOverlay);

        craftingGridHelper.setInputs(guiItemStacks, ingredients.getInputs(ItemStack.class));
        guiItemStacks.set(OUTPUT_SLOTS, ingredients.getOutputs(ItemStack.class).get(0));
    }
}
