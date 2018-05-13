package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 *
 */
final class ShapedRollingMachineRecipe implements IRollingMachineRecipe {
    private final int width;
    private final int height;
    private final List<@NonNull Ingredient> ingredients;
    private final ItemStack output;
    private final boolean allowFlip;
    private final int time;

    ShapedRollingMachineRecipe(int width, int height, List<@NonNull Ingredient> items, ItemStack output, int time, boolean allowFlip) {
        this.height = height;
        this.width = width;
        this.ingredients = items;
        this.output = output;
        this.time = time;
        this.allowFlip = allowFlip;
    }

    @Override
    public boolean test(@NonNull InventoryCrafting inv) {
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
                int x = i + xStart;
                int y = j + yStart;
                Ingredient ingredient = Ingredient.EMPTY;

                if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
                    if (backwards) {
                        ingredient = this.ingredients.get(this.width - x - 1 + y * this.width);
                    } else {
                        ingredient = this.ingredients.get(x + y * this.width);
                    }
                }

                if (!ingredient.apply(grid.getStackInRowAndColumn(i, j))) {
                    return false;
                }
            }
        }

        return true;
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
