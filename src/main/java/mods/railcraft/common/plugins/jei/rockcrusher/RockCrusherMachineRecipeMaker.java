/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rockcrusher;

import mezz.jei.api.IJeiHelpers;
import mods.railcraft.api.crafting.ICrusherRecipe;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RockCrusherMachineRecipeMaker {

    public static List<RockCrusherRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        List<RockCrusherRecipeWrapper> wrappers = new ArrayList<>();
        Collection<ICrusherRecipe> rawRecipes = RockCrusherCraftingManager.getInstance().getRecipes();
        for (ICrusherRecipe recipe : rawRecipes) {
            wrappers.add(new RockCrusherRecipeWrapper(recipe));
        }

        return wrappers;
    }

    private RockCrusherMachineRecipeMaker() {
    }

}
