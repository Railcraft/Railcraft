/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.ICokeOvenCraftingManager;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.fluids.FluidStack;

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

        public CokeOvenRecipe(ItemStack input, boolean matchDamage, boolean matchNBT, ItemStack output, FluidStack fluidOutput, int cookTime) {
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
        public ItemStack getOutput() {
            if (output == null) {
                return null;
            }
            return output.copy();
        }

        @Override
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
    }

    @Override
    public void addRecipe(ItemStack input, boolean matchDamage, boolean matchNBT, ItemStack output, FluidStack fluidOutput, int cookTime) {
        if (input == null) return;
        recipes.add(new CokeOvenRecipe(input, matchDamage, matchNBT, output, fluidOutput, cookTime));

//        Game.log(Level.DEBUG, "Adding Coke Oven recipe: {0}, {1}, {2}", input.getItem().getClass().getName(), input, input.getItemDamage());
    }

    @Override
    public ICokeOvenRecipe getRecipe(ItemStack input) {
        if (input == null) return null;
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
