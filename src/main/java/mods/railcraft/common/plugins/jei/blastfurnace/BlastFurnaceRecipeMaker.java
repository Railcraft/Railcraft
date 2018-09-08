/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.blastfurnace.rockcrusher;

import mezz.jei.api.IJeiHelpers;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.ICrusherRecipe;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class BlastFurnaceRecipeMaker {

    public static List<BlastFurnaceRecipeWrapper> getRecipes(IJeiHelpers jeiHelpers) {
        List<BlastFurnaceRecipeWrapper> wrappers = new ArrayList<>();
        Collection<IBlastFurnaceRecipe> rawRecipes = BlastFurnaceCraftingManager.getInstance().getRecipes();
        for (IBlastFurnaceRecipe recipe : rawRecipes) {
            wrappers.add(new BlastFurnaceRecipeWrapper(recipe));
        }

        return wrappers;
    }

    private BlastFurnaceRecipeMaker() {
    }

}
