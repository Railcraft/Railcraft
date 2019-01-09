/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.ISimpleRecipe;
import mods.railcraft.client.gui.GuiTools;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 1/9/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RecipeProvider<R extends ISimpleRecipe> {

    protected final IJeiHelpers helpers;

    protected RecipeProvider(IModRegistry registry) {this.helpers = registry.getJeiHelpers();}

    protected abstract List<R> getRawRecipes();

    protected abstract IRecipeWrapper wrap(R recipe);

    public List<IRecipeWrapper> getRecipes() {
        return getRawRecipes().stream()
                .filter(r -> r.getInput().getMatchingStacks().length > 0)
                .map(this::wrap).collect(Collectors.toList());
    }

    protected void drawTickTime(R recipe, Minecraft minecraft, int x, int y, boolean split) {
        ItemStack input = helpers.getStackHelper().toItemStackList(recipe.getInput()).stream().findFirst().orElse(ItemStack.EMPTY);
        if (InvTools.nonEmpty(input)) {
            int cookTime = recipe.getTickTime(input);
            String time = LocalizationPlugin.translate("gui.railcraft.jei.seconds",
                    HumanReadableNumberFormatter.format((double) cookTime / RailcraftConstants.TICKS_PER_SECOND));
            for (String line : split ? time.split("\\s") : new String[]{time}) {
                GuiTools.drawStringCenteredAtPos(minecraft.fontRenderer, line, x, y);
                y += minecraft.fontRenderer.FONT_HEIGHT;
            }
        }
    }
}
