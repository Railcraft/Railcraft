/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.api.crafting.ISimpleRecipe;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
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
        newFuel(RailcraftItems.FIRESTONE_REFINED).time(stack -> stack.getItem().getItemBurnTime(stack)).register();
        newFuel(RailcraftItems.FIRESTONE_CRACKED).time(stack -> stack.getItem().getItemBurnTime(stack)).register();
        newFuel("blockCharcoal").register();
    }

    @Override
    public IFuelBuilder newFuel(Object input) {
        FuelBuilder builder = new FuelBuilder(Ingredients.from(input));
        CraftingPlugin.tryGuessName(input, builder);
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
    public Optional<ISimpleRecipe> getFuel(ItemStack stack) {
        if (InvTools.isEmpty(stack)) return Optional.empty();
        return fuels.stream()
                .filter(fuel -> fuel.getInput().test(stack))
                .findFirst();
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack stack) {
        if (InvTools.isEmpty(stack)) return Optional.empty();
        return recipes.stream()
                .filter(recipe -> recipe.getInput().test(stack))
                .findFirst();
    }

    private class FuelBuilder extends SimpleRecipeBuilder<IFuelBuilder> implements IFuelBuilder {
        public FuelBuilder(Ingredient input) {
            super("Blast Furnace Fuel", input, FuelPlugin::getBurnTime);
        }

        @Override
        public IFuelBuilder name(@Nullable ResourceLocation name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        @Override
        protected void registerRecipe() {
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
                public int getTickTime(ItemStack input) {
                    return timeFunction.apply(input);
                }
            });
        }
    }

    private class RecipeBuilder extends SimpleRecipeBuilder<IRecipeBuilder> implements IRecipeBuilder {
        private int slagOutput;
        private ItemStack output = ItemStack.EMPTY;

        public RecipeBuilder(Ingredient input) {
            super("Blast Furnace", input, stack -> SMELT_TIME);
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
        public void registerRecipe() {
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
                public int getTickTime(ItemStack input) {
                    return timeFunction.apply(input);
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
        }
    }
}
