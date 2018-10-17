/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

/**
 *
 */
public final class ShapedRollingMachineRecipe implements IRollingMachineRecipe {
    private final int width;
    private final int height;
    private final List<Ingredient> ingredients;
    private final ItemStack output;
    private final boolean allowFlip;
    private final int time;

    ShapedRollingMachineRecipe(int width, int height, List<Ingredient> items, ItemStack output, int time, boolean allowFlip) {
        this.height = height;
        this.width = width;
        this.ingredients = items;
        this.output = output;
        this.time = time;
        this.allowFlip = allowFlip;
    }

    @Override
    public boolean test(InventoryCrafting inv) {
        for (int i = 0; i <= inv.getWidth() - this.width; ++i) {
            for (int j = 0; j <= inv.getHeight() - this.height; ++j) {
                if (allowFlip && this.checkMatch(inv, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting grid, int xStart, int yStart, boolean backwards) {
        for (int i = 0; i < grid.getWidth(); ++i) {
            for (int j = 0; j < grid.getHeight(); ++j) {
                int k = i - xStart;
                int l = j - yStart;
                Ingredient ingredient = Ingredient.EMPTY;

                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (backwards) {
                        ingredient = this.ingredients.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.ingredients.get(k + l * this.width);
                    }
                }

                if (!ingredient.apply(grid.getStackInRowAndColumn(i, j))) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getWidth() {
        return width;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getSampleOutput() {
        return output;
    }

    @Override
    public int getTime() {
        return time;
    }
}
