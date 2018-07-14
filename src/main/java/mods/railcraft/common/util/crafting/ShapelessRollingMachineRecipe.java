package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ShapelessRollingMachineRecipe implements IRollingMachineRecipe {
    private final List<@NotNull Ingredient> ingredients;
    private final ItemStack output;

    private final int time;

    ShapelessRollingMachineRecipe(List<@NotNull Ingredient> items, ItemStack output, int time) {
        this.ingredients = items;
        this.output = output;
        this.time = time;
    }

    @Override
    public boolean test(@NotNull InventoryCrafting inv) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                stacks.add(inv.getStackInSlot(i));
            }
        }
        if (stacks.isEmpty())
            return false;
        return RecipeMatcher.findMatches(stacks, ingredients) != null;
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
