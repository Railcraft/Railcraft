/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IRollingMachineRecipe;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class RollingMachineRecipeMaker {

    public static List<IRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();
        Collection<IRollingMachineRecipe> rawRecipes = RollingMachineCraftingManager.getInstance().getRecipes();
        for (IRollingMachineRecipe recipe : rawRecipes) {
            //TODO
        }

        return Collections.emptyList();
    }

    private RollingMachineRecipeMaker() {
    }

}
