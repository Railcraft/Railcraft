/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import com.google.common.base.Preconditions;
import mods.railcraft.api.crafting.IRollingMachineCrafter;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public enum RollingMachineCrafter implements IRollingMachineCrafter {
    INSTANCE;
    private static final ResourceLocation DEFAULT_GROUP = new ResourceLocation("railcraft", "rolling");
    private final List<IRollingRecipe> recipes = new ArrayList<>();

    /**
     * This isn't very elegant, but it works. The main problem is that the Rolling Machine recipes are very
     * generic and tend to have lots of inter-mod conflicts.
     */
    public static void copyRecipesToWorkbench() {
        ForgeRegistries.RECIPES.registerAll(INSTANCE.getRecipes().toArray(new IRecipe[0]));
    }

    @Override
    public IRollingMachineRecipeBuilder newRecipe(ItemStack output) {
        return new RollingMachineRecipeBuilder(output).name(output);
    }

    @Override
    public Optional<IRollingRecipe> getRecipe(InventoryCrafting inv, World world) {
        return recipes.stream().filter(r -> r.matches(inv, world)).findFirst();
    }

    @Override
    public List<IRollingRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    public List<IRollingRecipe> getValidRecipes() {
        return recipes.stream().filter(recipe -> {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            return ingredients.stream()
                    .flatMap(Streams.toType(OreIngredient.class))
                    .noneMatch(ore -> ore.getMatchingStacks().length == 0);
        }).collect(Collectors.toList());
    }

    private class RollingMachineRecipeBuilder extends RecipeBuilder<IRollingMachineRecipeBuilder> implements IRollingMachineRecipeBuilder {
        private ResourceLocation group = DEFAULT_GROUP;
        private IRecipe recipe;

        public RollingMachineRecipeBuilder(ItemStack output) {
            super("Rolling Machine");
            addFeature(new SingleItemStackOutputFeature<>(this, true));
            addFeature(new TimeFeature<>(this, stack -> DEFAULT_PROCESS_TIME));
            output(output);
        }

        @Override
        public IRollingMachineRecipeBuilder group(ResourceLocation group) {
            this.group = group;
            return this;
        }

        @Override
        protected void checkArguments() {
            ItemStack output = getOutput();
            if (name == null && InvTools.nonEmpty(output))
                this.name = CraftingPlugin.getNameFromOutput(output);
            super.checkArguments();
            getFeature(TimeFeature.class).ifPresent(feature -> {
                ToIntFunction<ItemStack> f = Code.cast(feature.timeFunction);
                Preconditions.checkArgument(f.applyAsInt(ItemStack.EMPTY) > 0,
                        "Time set to zero.");
            });
        }

        @Override
        protected void registerRecipe() {
            final ToIntFunction<ItemStack> timeFunction = getTimeFunction();
            recipes.add(new RollingRecipe(recipe.setRegistryName(name), timeFunction.applyAsInt(ItemStack.EMPTY)));
        }

        @Override
        public void recipe(IRecipe recipe) {
            this.recipe = recipe;
            register();
        }

        @Override
        public void shaped(Object... recipeArray) {
            try {
                recipe(CraftingPlugin.makeShapedRecipe(name, group, getOutput(), recipeArray));
            } catch (InvalidRecipeException ex) {
                handleException(ex);
            }
        }

        @Override
        public void shapeless(Object... recipeArray) {
            try {
                recipe(CraftingPlugin.makeShapelessRecipe(name, group, getOutput(), recipeArray));
            } catch (InvalidRecipeException ex) {
                handleException(ex);
            }
        }

        private void handleException(Throwable ex) {
            Game.log().msg(Level.WARN,
                    "Tried, but failed to register {0} as a Rolling Machine recipe. Reason: {1}",
                    name, ex.getMessage());
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
        public @Nullable ResourceLocation getRegistryName() {return getRecipe().getRegistryName();}

        @Override
        public Class<IRecipe> getRegistryType() {return getRecipe().getRegistryType();}
    }
}
