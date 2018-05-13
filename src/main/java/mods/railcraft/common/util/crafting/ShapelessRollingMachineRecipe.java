package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IRollingMachineRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.RecipeMatcher;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 */
final class ShapelessRollingMachineRecipe implements IRollingMachineRecipe {
    private final List<@NonNull Ingredient> ingredients;
    private final ItemStack output;
    private static final Field NON_NULL_LIST_FIELD = InventoryCrafting.class.getDeclaredFields()[0];

    private final int time;

    ShapelessRollingMachineRecipe(List<@NonNull Ingredient> items, ItemStack output, int time) {
        this.ingredients = items;
        this.output = output;
        this.time = time;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean test(@NonNull InventoryCrafting inv) {
        try {
            List<ItemStack> stacks = (List<ItemStack>) NON_NULL_LIST_FIELD.get(inv);
            return RecipeMatcher.findMatches(stacks, ingredients) != null;
        } catch (IllegalAccessException error) {
            return false;
        }
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
