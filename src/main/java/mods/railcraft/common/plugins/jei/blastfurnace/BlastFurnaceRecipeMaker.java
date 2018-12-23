/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.blastfurnace;

import mezz.jei.api.IJeiHelpers;
import mods.railcraft.api.crafting.Crafters;

import java.util.List;
import java.util.stream.Collectors;

public final class BlastFurnaceRecipeMaker {

    public static List<BlastFurnaceRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        return Crafters.blastFurnace().getRecipes().stream()
                .map(recipe -> new BlastFurnaceRecipeWrapper(jeiHelpers, recipe)).collect(Collectors.toList());
    }

    private BlastFurnaceRecipeMaker() {
    }

}
