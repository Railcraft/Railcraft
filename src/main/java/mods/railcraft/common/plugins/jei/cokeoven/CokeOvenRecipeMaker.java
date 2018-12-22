/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mods.railcraft.api.crafting.Crafters;

import java.util.List;
import java.util.stream.Collectors;

public class CokeOvenRecipeMaker {

    private CokeOvenRecipeMaker() {
    }

    public static List<CokeOvenWrapper> getCokeOvenRecipe(IModRegistry registry) {
        IJeiHelpers helper = registry.getJeiHelpers();
        return Crafters.cokeOven().getRecipes().stream()
                .map(recipe -> new CokeOvenWrapper(helper, recipe)).collect(Collectors.toList());
    }

}
