package mods.railcraft.common.plugins.jei.cokeoven;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.common.util.crafting.CokeOvenCraftingManager;

import java.util.ArrayList;
import java.util.List;

public class CokeOvenRecipeMaker {

    private CokeOvenRecipeMaker() {
        }

    public static List<CokeOvenWrapper> getCokeOvenRecipe(IModRegistry registry) {
        IJeiHelpers helper = registry.getJeiHelpers();
        List<CokeOvenWrapper> recipes = new ArrayList<>();
        for (ICokeOvenRecipe recipe : CokeOvenCraftingManager.getInstance().getRecipes()) {
            recipes.add(new CokeOvenWrapper(helper, recipe));
        }
        return recipes;
    }

}
