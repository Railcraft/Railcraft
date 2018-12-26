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
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.api.crafting.ISimpleRecipe;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum BlastFurnaceCrafter implements IBlastFurnaceCrafter {
    INSTANCE;
    private final List<@NotNull IRecipe> recipes = new ArrayList<>();
    private final List<@NotNull ISimpleRecipe> fuels = new ArrayList<>();

    public void postInit() {
        newFuel(ThaumcraftPlugin.ITEMS.get("alumentum", 0)).name("thaumcraft:alumentum").register();
        newFuel(EnumGeneric.BLOCK_COKE.getStack()).name("railcraft:block_coke").register();
        newFuel(new ItemStack(Items.COAL, 1, 1)).name("minecraft:charcoal").register();
        newFuel(RailcraftItems.FIRESTONE_REFINED).register();
        newFuel(RailcraftItems.FIRESTONE_CRACKED).register();
        newFuel("blockCharcoal")
                .time(FuelPlugin.getBurnTime(new ItemStack(Blocks.COAL_BLOCK, 1))).register();
    }

    @Override
    public IFuelBuilder newFuel(Object input) {
        FuelBuilder builder = new FuelBuilder(Ingredients.from(input));
        CraftingPlugin.tryGuessName(input, builder);
        if (input instanceof ItemStack) {
            builder.time((ItemStack) input);
        } else if (input instanceof IRailcraftObjectContainer) {
            IRailcraftObjectContainer<?> container = Code.cast(input);
            builder.time(container.getWildcard());
        }
        return builder;
    }

    @Override
    public IRecipeBuilder newRecipe(Object input) {
        RecipeBuilder builder = new RecipeBuilder(Ingredients.from(input));
        CraftingPlugin.tryGuessName(input, builder);
        return builder;
    }

    @Deprecated
    public void addRecipe(String name, Ingredient input, int cookTime, ItemStack output, int slagOutput) {
        newRecipe(input).name(name).time(cookTime).output(output).slagOutput(1).register();
    }

    @Override
    public List<@NotNull IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public List<@NotNull ISimpleRecipe> getFuels() {
        return CollectionTools.removeOnlyList(fuels);
    }

    @Override
    public int getCookTime(ItemStack stack) {
        return fuels.stream()
                .filter(fuel -> fuel.getInput().test(stack))
                .findFirst()
                .map(ISimpleRecipe::getTickTime)
                .orElse(0);
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack stack) {
        return recipes.stream()
                .filter(recipe -> recipe.getInput().test(stack))
                .findFirst();
    }

    private class FuelBuilder implements IFuelBuilder {
        private final Ingredient input;
        private ResourceLocation name;
        private int heatValue;
        private boolean registered;

        public FuelBuilder(Ingredient input) {
            this.input = input;
            CraftingPlugin.addBuilder(this);
        }

        @Override
        public IFuelBuilder name(@Nullable ResourceLocation name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        @Override
        public IFuelBuilder time(int ticks) {
            this.heatValue = ticks;
            return this;
        }

        @Override
        public IFuelBuilder time(ItemStack fuel) {
            this.heatValue = FuelPlugin.getBurnTime(fuel);
            return this;
        }

        @Override
        public void register() {
            registered = true;
            try {
                Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                        "Fuel input was null or empty.");
                Preconditions.checkArgument(name != null, "Fuel name not set.");
                Preconditions.checkArgument(heatValue > 0, "Heat value was zero.");
                fuels.add(new ISimpleRecipe() {
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
                        return heatValue;
                    }
                });
            } catch (Throwable ex) {
                Game.log(Level.WARN,
                        "Tried, but failed to register {0} as a Blast Furnace fuel. Reason: {1}",
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

    private class RecipeBuilder implements IRecipeBuilder {
        private final Ingredient input;
        private int slagOutput;
        private ItemStack output = ItemStack.EMPTY;
        private ResourceLocation name;
        private int cookTime = SMELT_TIME;
        private boolean registered;

        public RecipeBuilder(Ingredient input) {
            this.input = input;
            CraftingPlugin.addBuilder(this);
        }

        @Override
        public IRecipeBuilder slagOutput(int num) {
            this.slagOutput = num;
            return this;
        }

        @Override
        public IRecipeBuilder output(ItemStack output) {
            this.output = output.copy();
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
                    public ItemStack getOutput() {
                        return output.copy();
                    }

                    @Override
                    public int getSlagOutput() {
                        return slagOutput;
                    }
                });
            } catch (Throwable ex) {
                Game.log(Level.WARN,
                        "Tried, but failed to register {0} as a Blast Furnace recipe. Reason: {1}",
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
