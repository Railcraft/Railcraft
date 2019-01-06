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
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

public enum CokeOvenCrafter implements ICokeOvenCrafter {
    INSTANCE;
    private final List<IRecipe> recipes = new ArrayList<>();

    @Override
    public ICokeOvenRecipeBuilder newRecipe(Object input) {
        return new CokeOvenRecipeBuilder(Ingredients.from(input)).name(input);
    }

    @Override
    public List<IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack input) {
        if (InvTools.isEmpty(input))
            return Optional.empty();
        return recipes.stream()
                .filter(r -> r.getInput().test(input))
                .findFirst();
    }

    private class CokeOvenRecipeBuilder extends RecipeBuilder<ICokeOvenRecipeBuilder> implements ICokeOvenRecipeBuilder {
        private @Nullable FluidStack outputFluid;

        public CokeOvenRecipeBuilder(Ingredient input) {
            super("Coke Oven");
            addFeature(new SingleInputFeature<>(this, input));
            addFeature(new SingleItemStackOutputFeature<>(this, false));
            addFeature(new TimeFeature<>(this, stack -> DEFAULT_COOK_TIME));
        }

        @Override
        public ICokeOvenRecipeBuilder fluid(@Nullable FluidStack outputFluid) {
            this.outputFluid = FluidTools.copy(outputFluid);
            return this;
        }

        @Override
        protected void checkArguments() {
            super.checkArguments();
            Preconditions.checkArgument(InvTools.nonEmpty(getOutput()) || Fluids.nonEmpty(outputFluid),
                    "No outputs defined.");
        }

        @Override
        protected void registerRecipe() {
            final Ingredient input = getInput();
            final ToIntFunction<ItemStack> timeFunction = getTimeFunction();
            final ItemStack output = getOutput();
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
                    return timeFunction.applyAsInt(input);
                }

                @Override
                public @Nullable FluidStack getFluidOutput() {
                    return FluidTools.copy(outputFluid);
                }

                @Override
                public ItemStack getOutput() {
                    return output.copy();
                }
            });
        }
    }

}
