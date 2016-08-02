package buildcraft.api.recipes;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IProgrammingRecipe {
    String getId();

    /**
     * Get a list (size at least width*height) of ItemStacks representing options.
     *
     * @param width  The width of the Programming Table panel.
     * @param height The height of the Programming Table panel.
     */
    List<ItemStack> getOptions(int width, int height);

    /**
     * Get the energy cost of a given option ItemStack.
     */
    int getEnergyCost(ItemStack option);

    /**
     * @param input The input stack.
     * @return Whether this recipe applies to the given input stack.
     */
    boolean canCraft(ItemStack input);

    /**
     * Craft the input ItemStack with the given option into an output ItemStack.
     *
     * @return The output ItemStack.
     */
    ItemStack craft(ItemStack input, ItemStack option);
}
