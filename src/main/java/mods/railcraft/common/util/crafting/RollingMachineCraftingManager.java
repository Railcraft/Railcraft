/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import com.google.common.collect.Lists;
import mods.railcraft.api.crafting.IRollingMachineCraftingManager;
import mods.railcraft.api.crafting.IRollingMachineRecipe;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachine;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.ArrayTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class RollingMachineCraftingManager implements IRollingMachineCraftingManager {

    private final List<IRollingMachineRecipe> recipes = new ArrayList<>();
    private static final ResourceLocation INVALID = new ResourceLocation("invalid", "invalid");
    private static final RollingMachineCraftingManager INSTANCE = new RollingMachineCraftingManager();

    static {

    }

    public static IRollingMachineCraftingManager getInstance() {
        return INSTANCE;
    }

    private RollingMachineCraftingManager() {
    }

    @Override
    public ShapedRecipeBuilder newShapedRecipeBuilder() {
        return new ShapedRecipeBuilderImpl();
    }

    @Override
    public ShapelessRecipeBuilder newShapelessRecipeBuilder() {
        return new ShapelessRecipeBuilderImpl();
    }

    @Override
    public IRollingMachineRecipe findMatching(InventoryCrafting inventoryCrafting) {
        for (IRollingMachineRecipe recipe : recipes) {
            if (recipe.test(inventoryCrafting)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public Collection<IRollingMachineRecipe> getRecipes() {
        return recipes;
    }

    public static void copyRecipesToWorkbench() {
        //TODO
        ForgeRegistries.RECIPES.registerAll(getInstance().getRecipes().toArray(new IRecipe[0]));
    }

    @Override
    public void addRecipe(IRollingMachineRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public void addRecipe(ItemStack result, Object... recipeArray) {
        CraftingPlugin.ProcessedRecipe processedRecipe;
        try {
            processedRecipe = CraftingPlugin.processRecipe(CraftingPlugin.RecipeType.SHAPED, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }

        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(processedRecipe.recipeArray);
        IRollingMachineRecipe recipe = newShapedRecipeBuilder()
                .ingredients(primer.input)
                .height(primer.height)
                .width(primer.width)
                .output(result)
                .allowsFlip(primer.mirrored)
                .build();
        addRecipe(recipe);
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

        IRollingMachineRecipe recipe = newShapelessRecipeBuilder()
                .ingredients(ArrayTools.transform(processedRecipe.recipeArray, CraftingHelper::getIngredient, Ingredient[]::new))
                .output(processedRecipe.result)
                .build();
        addRecipe(recipe);
    }

    private static abstract class RecipeBuilderImpl<S extends RecipeBuilder<S>> implements RecipeBuilder<S> {
        @MonotonicNonNull List<Ingredient> ingredients;
        @MonotonicNonNull ItemStack output;
        int time = TileRollingMachine.PROCESS_TIME;

        @SuppressWarnings("unchecked")
        S self() {
            return (S) this;
        }

        @Override
        public S ingredients(Ingredient... ingredients) {
            this.ingredients = Lists.newArrayList(ingredients);
            return self();
        }

        @Override
        public S ingredients(Iterable<Ingredient> ingredients) {
            this.ingredients = Lists.newArrayList(ingredients);
            return self();
        }

        @Override
        public S output(ItemStack output) {
            this.output = output;
            return self();
        }

        @Override
        public S time(int time) {
            this.time = time;
            return self();
        }

        void checkArgs() throws IllegalArgumentException {
            checkNotNull(ingredients, "ingredients");
            checkNotNull(output, "output");
            checkArgument(time > 0, "time must be positive");
        }
    }

    private static final class ShapedRecipeBuilderImpl extends RecipeBuilderImpl<ShapedRecipeBuilder> implements ShapedRecipeBuilder {
        private int height;
        private int width;
        private boolean allowsFlip = false;

        @Override
        public ShapedRecipeBuilder height(int height) {
            this.height = height;
            return this;
        }

        @Override
        public ShapedRecipeBuilder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public ShapedRecipeBuilder grid(Ingredient[][] ingredients) {
            this.ingredients = Arrays.asList(ArrayTools.flatten(ingredients));
            return this;
        }

        @Override
        public ShapedRecipeBuilder allowsFlip(boolean flip) {
            this.allowsFlip = flip;
            return this;
        }

        @Override
        public IRollingMachineRecipe build() throws IllegalArgumentException {
            checkArgs();
            checkArgument(height > 0, "height must be positive");
            checkArgument(width > 0, "width must be positive");
            return new ShapedRollingMachineRecipe(width, height, ingredients, output, time, allowsFlip);
        }

        @Override
        public void buildAndRegister() throws IllegalArgumentException {
            RollingMachineCraftingManager.getInstance().addRecipe(build());
        }
    }

    private static final class ShapelessRecipeBuilderImpl extends RecipeBuilderImpl<ShapelessRecipeBuilder> implements ShapelessRecipeBuilder {
        @Override
        public ShapelessRecipeBuilder add(Ingredient ingredient) {
            if (ingredients == null) {
                ingredients = new ArrayList<>();
            }
            ingredients.add(ingredient);
            return this;
        }

        @Override
        public IRollingMachineRecipe build() throws IllegalArgumentException {
            return new ShapelessRollingMachineRecipe(ingredients, output, time);
        }

        @Override
        public void buildAndRegister() throws IllegalArgumentException {
            RollingMachineCraftingManager.getInstance().addRecipe(build());
        }
    }
}
