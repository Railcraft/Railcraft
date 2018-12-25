/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IRollingMachineCrafter;

import java.util.ArrayList;
import java.util.List;

public final class RollingMachineRecipeMaker {

    public static List<IRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();
        List<IRollingMachineCrafter.IRollingRecipe> rawRecipes = Crafters.rollingMachine().getRecipes();
        for (IRollingMachineCrafter.IRollingRecipe recipe : rawRecipes) {
            // TODO this needs rewriting for the new recipes
        }

        return wrappers;
    }

    private RollingMachineRecipeMaker() {
    }

}
