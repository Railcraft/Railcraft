/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import joptsimple.internal.Objects;
import mods.railcraft.api.crafting.ICokeOvenCraftingManager;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CokeOvenCraftingManager implements ICokeOvenCraftingManager {

    private final List<CokeOvenRecipe> recipes = new ArrayList<CokeOvenRecipe>();

    public static ICokeOvenCraftingManager getInstance() {
        return RailcraftCraftingManager.cokeOven;
    }

    @Override
    public List<? extends ICokeOvenRecipe> getRecipes() {
        return recipes;
    }

    public static class CokeOvenRecipe implements ICokeOvenRecipe {

        private final ItemStack input;
        private final boolean matchDamage;
        private final boolean matchNBT;
        private final FluidStack fluidOutput;
        private final int cookTime;
        private final ItemStack output;

        public CokeOvenRecipe(ItemStack input, boolean matchDamage, boolean matchNBT,
                              @Nullable ItemStack output,
                              @Nullable FluidStack fluidOutput, int cookTime) {
            this.input = input;
            this.matchDamage = matchDamage;
            this.matchNBT = matchNBT;
            this.output = output;
            this.fluidOutput = fluidOutput;
            this.cookTime = cookTime;
        }

        @Override
        public ItemStack getInput() {
            return input.copy();
        }

        @Override
        @Nullable
        public ItemStack getOutput() {
            if (InvTools.isEmpty(output)) {
                return InvTools.emptyStack();
            }
            return output.copy();
        }

        @Override
        @Nullable
        public FluidStack getFluidOutput() {
            if (fluidOutput != null) {
                return fluidOutput.copy();
            }
            return null;
        }

        @Override
        public int getCookTime() {
            return cookTime;
        }

        @Override
        public String toString() {
            return String.format("Coke Oven Recipe: %s -> %s & %s", InvTools.toString(input), InvTools.toString(output), FluidTools.toString(fluidOutput));
        }
    }

    @Override
    public void addRecipe(ItemStack input, boolean matchDamage, boolean matchNBT, ItemStack output, FluidStack fluidOutput, int cookTime) {
        Objects.ensureNotNull(input);
        recipes.add(new CokeOvenRecipe(input, matchDamage, matchNBT, output, fluidOutput, cookTime));

//        Game.log(Level.DEBUG, "Adding Coke Oven recipe: {0}, {1}, {2}", input.getItem().getClass().getName(), input, input.getItemDamage());
    }

    @Override
    public ICokeOvenRecipe getRecipe(ItemStack input) {
        if (InvTools.isEmpty(input)) return null;
        for (CokeOvenRecipe r : recipes) {
            if (!r.matchDamage || InvTools.isWildcard(r.input)) continue;
            if (InvTools.isItemEqual(input, r.input, true, r.matchNBT))
                return r;
        }
        for (CokeOvenRecipe r : recipes) {
            if (InvTools.isItemEqual(input, r.input, r.matchDamage, r.matchNBT))
                return r;
        }
        return null;
    }

}
