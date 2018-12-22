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
import mods.railcraft.api.crafting.IRollingMachineRecipe;
import mods.railcraft.common.util.crafting.ShapedRollingMachineRecipe;
import mods.railcraft.common.util.crafting.ShapelessRollingMachineRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RollingMachineRecipeMaker {

    public static List<IRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();
        Collection<IRollingMachineRecipe> rawRecipes = Crafters.rollingMachine().getRecipes();
        for (IRollingMachineRecipe recipe : rawRecipes) {
            if(recipe instanceof ShapedRollingMachineRecipe)
                wrappers.add(new ShapedRollingMachineRecipeWrapper((ShapedRollingMachineRecipe)recipe, jeiHelpers.getStackHelper()));
            else if(recipe instanceof ShapelessRollingMachineRecipe)
                wrappers.add(new ShapelessRollingMachineRecipeWrapper((ShapelessRollingMachineRecipe)recipe, jeiHelpers.getStackHelper()));
        }

        return wrappers;
    }

    private RollingMachineRecipeMaker() {
    }

}
