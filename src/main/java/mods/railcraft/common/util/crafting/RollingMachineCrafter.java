/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineCrafter;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum RollingMachineCrafter implements IRollingMachineCrafter {
    INSTANCE;
    private static ResourceLocation DEFAULT_GROUP = new ResourceLocation("railcraft", "rolling");
    private final List<IRollingRecipe> recipes = new ArrayList<>();

    /**
     * This isn't very elegant, but it works. The main problem is that the Rolling Machine recipes are very
     * generic and tend to have lots of inter-mod conflicts.
     */
    public static void copyRecipesToWorkbench() {
        ForgeRegistries.RECIPES.registerAll(INSTANCE.getRecipes().toArray(new IRecipe[0]));
    }

    @Override
    public IRecipeBuilder newRecipe(ItemStack output) {
        return new RecipeBuilder(output);
    }

    @Override
    public Optional<IRollingRecipe> getRecipe(InventoryCrafting inv, World world) {
        return recipes.stream().filter(r -> r.matches(inv, world)).findFirst();
    }

    @Override
    public List<IRollingRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    private class RecipeBuilder implements IRecipeBuilder {
        private final ItemStack output;
        private ResourceLocation name;
        private ResourceLocation group = DEFAULT_GROUP;
        private int time = DEFAULT_PROCESS_TIME;

        public RecipeBuilder(ItemStack output) {
            this.output = output.copy();
            this.name = CraftingPlugin.getName(output);
        }

        @Override
        public IRecipeBuilder name(ResourceLocation name) {
            this.name = name;
            return this;
        }

        @Override
        public IRecipeBuilder group(ResourceLocation group) {
            this.group = group;
            return this;
        }

        @Override
        public IRecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public void recipe(IRecipe recipe) {
            recipes.add(new RollingRecipe(recipe.setRegistryName(name), time));
        }

        @Override
        public void shaped(Object... recipeArray) {
            try {
                recipe(CraftingPlugin.makeShapedRecipe(name, group, output, recipeArray));
            } catch (InvalidRecipeException ex) {
                Game.logTrace(Level.WARN, ex.getRawMessage());
            }
        }

        @Override
        public void shapeless(Object... recipeArray) {
            try {
                recipe(CraftingPlugin.makeShapelessRecipe(name, group, output, recipeArray));
            } catch (InvalidRecipeException ex) {
                Game.logTrace(Level.WARN, ex.getRawMessage());
            }
        }
    }

    private static class RollingRecipe implements IRollingRecipe {
        private final IRecipe recipe;
        private final int tickTime;

        private RollingRecipe(IRecipe recipe, int tickTime) {
            this.recipe = recipe;
            this.tickTime = tickTime;
        }

        @Override
        public int getTickTime() {
            return tickTime;
        }

        private IRecipe getRecipe() {
            return recipe;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {return getRecipe().matches(inv, worldIn);}

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {return getRecipe().getCraftingResult(inv);}

        @Override
        public boolean canFit(int width, int height) {return getRecipe().canFit(width, height);}

        @Override
        public ItemStack getRecipeOutput() {return getRecipe().getRecipeOutput();}

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {return getRecipe().getRemainingItems(inv);}

        @Override
        public NonNullList<Ingredient> getIngredients() {return getRecipe().getIngredients();}

        @Override
        public boolean isDynamic() {return getRecipe().isDynamic();}

        @Override
        public String getGroup() {return getRecipe().getGroup();}

        @Override
        public IRecipe setRegistryName(ResourceLocation name) {return getRecipe().setRegistryName(name);}

        @Override
        @Nullable
        public ResourceLocation getRegistryName() {return getRecipe().getRegistryName();}

        @Override
        public Class<IRecipe> getRegistryType() {return getRecipe().getRegistryType();}
    }
}
