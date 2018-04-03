/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RollingMachineCraftingManager implements IRollingMachineCraftingManager {

    private final List<IRecipe> recipes = new ArrayList<>();
    private static final ResourceLocation INVALID = new ResourceLocation("invalid", "invalid");

    public static IRollingMachineCraftingManager instance() {
        return RailcraftCraftingManager.rollingMachine;
    }

    public static void copyRecipesToWorkbench() {
        ForgeRegistries.RECIPES.registerAll(instance().getRecipeList().toArray(new IRecipe[0]));
    }

    @Override
    public void addRecipe(IRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public void addRecipe(@Nullable ItemStack result, Object... recipeArray) {
        CraftingPlugin.ProcessedRecipe processedRecipe;
        try {
            processedRecipe = CraftingPlugin.processRecipe(CraftingPlugin.RecipeType.SHAPED, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.isOreRecipe) {
            IRecipe recipe = new ShapedOreRecipe(INVALID, processedRecipe.result, processedRecipe.recipeArray);
            addRecipe(recipe);
        } else
            addRecipe(CraftingPlugin.makeVanillaShapedRecipe(processedRecipe.result, processedRecipe.recipeArray));
    }

    @Override
    public void addShapelessRecipe(@Nullable ItemStack result, Object... recipeArray) {
        CraftingPlugin.ProcessedRecipe processedRecipe;
        try {
            processedRecipe = CraftingPlugin.processRecipe(CraftingPlugin.RecipeType.SHAPELESS, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.isOreRecipe) {
            addRecipe(new ShapelessOreRecipe(INVALID, processedRecipe.result, processedRecipe.recipeArray));
        } else
            addRecipe(CraftingPlugin.makeVanillaShapelessRecipe(processedRecipe.result, processedRecipe.recipeArray));
    }

    @Override
    public ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {
        for (IRecipe irecipe : recipes) {
            if (irecipe.matches(inv, world)) {
                return irecipe.getCraftingResult(inv);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<IRecipe> getRecipeList() {
        return recipes;
    }
}
