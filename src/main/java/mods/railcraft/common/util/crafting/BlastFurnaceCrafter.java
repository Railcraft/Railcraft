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
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.api.crafting.ISimpleRecipe;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.items.ItemCoke;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;

public enum BlastFurnaceCrafter implements IBlastFurnaceCrafter {
    INSTANCE;
    private final List<@NotNull IRecipe> recipes = new ArrayList<>();
    private final List<@NotNull ISimpleRecipe> fuels = new ArrayList<>();

    public void initFuel() {
        newFuel(Ingredients.from("fuelCoke"))
                .name("railcraft:fuel_coke")
                .time(ItemCoke.COKE_HEAT)
                .register();
        newFuel(ThaumcraftPlugin.ITEMS.get("alumentum", 0)).name("thaumcraft:alumentum").register();
        newFuel(EnumGeneric.BLOCK_COKE.getStack()).name("railcraft:block_coke").register();
        newFuel(new ItemStack(Items.COAL, 1, 1)).name("minecraft:charcoal").register();
        newFuel(RailcraftItems.FIRESTONE_REFINED).time(stack -> stack.getItem().getItemBurnTime(stack)).register();
        newFuel(RailcraftItems.FIRESTONE_CRACKED).time(stack -> stack.getItem().getItemBurnTime(stack)).register();
        newFuel("blockCharcoal").register();
    }

    @Override
    public IFuelBuilder newFuel(Object input) {
        return new FuelBuilder(Ingredients.from(input)).name(input);
    }

    @Override
    public IBlastFurnaceRecipeBuilder newRecipe(Object input) {
        return new BlastFurnaceRecipeBuilder(Ingredients.from(input)).name(input);
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

    private class FuelBuilder extends RecipeBuilder<IFuelBuilder> implements IFuelBuilder {
        public FuelBuilder(Ingredient input) {
            super("Blast Furnace Fuel");
            addFeature(new SingleInputFeature<>(this, input));
            addFeature(new TimeFeature<>(this, FuelPlugin::getBurnTime));
        }

        @Override
        protected void registerRecipe() {
            final Ingredient input = getInput();
            final ToIntFunction<ItemStack> timeFunction = getTimeFunction();
            fuels.add(new ISimpleRecipe() {
                @Override
                public ResourceLocation getName() {
                    return Objects.requireNonNull(name);
                }

                @Override
                public Ingredient getInput() {
                    return input;
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return timeFunction.applyAsInt(input);
                }
            });
        }
    }

    private class BlastFurnaceRecipeBuilder extends RecipeBuilder<IBlastFurnaceRecipeBuilder> implements IBlastFurnaceRecipeBuilder {
        private int slagOutput;

        public BlastFurnaceRecipeBuilder(Ingredient input) {
            super("Blast Furnace");
            addFeature(new SingleInputFeature<>(this, input));
            addFeature(new SingleItemStackOutputFeature<>(this, false));
            addFeature(new TimeFeature<>(this, stack -> SMELT_TIME));
        }

        @Override
        public IBlastFurnaceRecipeBuilder slagOutput(int num) {
            this.slagOutput = num;
            return this;
        }

        @Override
        protected void checkArguments() {
            super.checkArguments();
            Preconditions.checkArgument(InvTools.nonEmpty(getOutput()) || slagOutput > 0,
                    "No outputs defined.");
        }

        @Override
        public void registerRecipe() {
            final Ingredient input = getInput();
            final ItemStack output = getOutput();
            final ToIntFunction<ItemStack> timeFunction = getTimeFunction();
            recipes.add(new IRecipe() {
                @Override
                public ResourceLocation getName() {
                    return Objects.requireNonNull(name);
                }

                @Override
                public Ingredient getInput() {
                    return input;
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return timeFunction.applyAsInt(input);
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
