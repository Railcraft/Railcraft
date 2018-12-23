/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum CokeOvenCrafter implements ICokeOvenCrafter {
    INSTANCE;
    private final List<IRecipe> recipes = new ArrayList<>();

    @Override
    public List<IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public void addRecipe(@Nullable ResourceLocation name, Ingredient input, ItemStack output, @Nullable FluidStack liquidOutput, int cookTime) {
        if (!input.test(ItemStack.EMPTY)) {
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
                    return FluidTools.copy(liquidOutput);
                }

                @Override
                public ItemStack getOutput() {
                    return output.copy();
                }
            });
        } else {
            Game.log(Level.WARN, "Tried, but failed to register {0} as a coke oven recipe", name);
        }
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack input) {
        return recipes.stream()
                .filter(r -> r.getInput().test(input))
                .findFirst();
    }

}
