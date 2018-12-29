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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
        if (InvTools.isEmpty(input))
            return Optional.empty();
        return recipes.stream()
                .filter(r -> r.getInput().test(input))
                .findFirst();
    }

    private class RecipeBuilder extends SimpleRecipeBuilder<IRecipeBuilder> implements IRecipeBuilder {
        private ItemStack output = ItemStack.EMPTY;
        private @Nullable FluidStack outputFluid;

        public RecipeBuilder(Ingredient input) {
            super("Coke Oven", input, stack -> DEFAULT_COOK_TIME);
        }

        @Override
        public IRecipeBuilder output(ItemStack output) {
            this.output = output.copy();
            return this;
        }

        @Override
        public IRecipeBuilder fluid(@Nullable FluidStack outputFluid) {
            this.outputFluid = FluidTools.copy(outputFluid);
            return this;
        }

        @Override
        protected void checkArguments() {
            super.checkArguments();
            Preconditions.checkArgument(InvTools.nonEmpty(output) || Fluids.isNotEmpty(outputFluid),
                    "No outputs defined.");
        }

        @Override
        protected void registerRecipe() {
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
