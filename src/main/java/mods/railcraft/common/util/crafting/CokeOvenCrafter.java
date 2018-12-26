/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import com.google.common.base.Preconditions;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum CokeOvenCrafter implements ICokeOvenCrafter {
    INSTANCE;
    private final List<IRecipe> recipes = new ArrayList<>();

    @Override
    public IRecipeBuilder newRecipe(Object input) {
        RecipeBuilder builder = new RecipeBuilder(Ingredients.from(input));
        CraftingPlugin.tryGuessName(input, builder);
        return builder;
    }

    @Override
    public List<IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack input) {
        return recipes.stream()
                .filter(r -> r.getInput().test(input))
                .findFirst();
    }

    private class RecipeBuilder implements IRecipeBuilder {
        private final Ingredient input;
        private ResourceLocation name;
        private ItemStack output = ItemStack.EMPTY;
        private FluidStack fluidOutput;
        private int cookTime = DEFAULT_COOK_TIME;
        private boolean registered;

        public RecipeBuilder(Ingredient input) {
            this.input = input;
            CraftingPlugin.addBuilder(this);
        }

        @Override
        public IRecipeBuilder output(ItemStack output) {
            this.output = output.copy();
            return this;
        }

        @Override
        public IRecipeBuilder fluid(@Nullable FluidStack outputFluid) {
            this.fluidOutput = FluidTools.copy(fluidOutput);
            return this;
        }

        @Override
        public IRecipeBuilder name(@Nullable ResourceLocation name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        @Override
        public IRecipeBuilder time(int ticks) {
            this.cookTime = ticks;
            return this;
        }

        @Override
        public void register() {
            registered = true;
            try {
                Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                        "Input was null or empty.");
                Preconditions.checkArgument(name != null, "Recipe name not set.");
                Preconditions.checkArgument(cookTime > 0, "Cook time was zero.");
                Preconditions.checkArgument(InvTools.nonEmpty(output) || Fluids.isNotEmpty(fluidOutput),
                        "No outputs defined.");
                recipes.add(new IRecipe() {
                    @Override
                    public ResourceLocation getName() {
                        return name;
                    }

                    @Override
                    public Ingredient getInput() {
                        return input;
                    }

                    @Override
                    public int getTickTime() {
                        return cookTime;
                    }

                    @Override
                    public @Nullable FluidStack getFluidOutput() {
                        return FluidTools.copy(fluidOutput);
                    }

                    @Override
                    public ItemStack getOutput() {
                        return output.copy();
                    }
                });
            } catch (Throwable ex) {
                Game.log(Level.WARN,
                        "Tried, but failed to register {0} as a Coke Oven recipe. Reason: {1}",
                        name, ex.getMessage());
            }
        }

        @Override
        public ResourceLocation getName() {
            return name;
        }

        @Override
        public boolean notRegistered() {
            return !registered;
        }
    }

}
