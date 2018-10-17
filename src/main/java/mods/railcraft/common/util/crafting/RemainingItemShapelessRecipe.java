/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.util.RecipeMatcher;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A shaped recipe which respects its ingredient's choice of remaining items.
 */
@SuppressWarnings("unused")
public final class RemainingItemShapelessRecipe extends ShapelessRecipes {

    private final ItemStack recipeOutput;
    /**
     * Is a List of ItemStack that composes the recipe.
     */
    public final NonNullList<Ingredient> recipeItems;
    private final String group;
    private @Nullable InventoryCrafting lastInv;
    private @Nullable int[] lastResult;

    public RemainingItemShapelessRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients) {
        super(group, output, ingredients);
        this.group = group;
        this.recipeOutput = output;
        this.recipeItems = ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        if (lastInv != inv) {
            matches(inv, null);
        }
        final int[] mappings = requireNonNull(lastResult);

        final int size = inv.getSizeInventory();
        NonNullList<ItemStack> ret = NonNullList.withSize(size, ItemStack.EMPTY);
        int index = 0;
        for (int i = 0; i < size; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                Ingredient ingredient = recipeItems.get(mappings[index]);
                if (ingredient instanceof IRemainderIngredient) {
                    ret.set(i, ((IRemainderIngredient) ingredient).getRemaining(itemstack));
                } else {
                    ret.set(i, ForgeHooks.getContainerItem(itemstack));
                }
                index++;
            }
        }

        return ret;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting inv, @Nullable World worldIn) {
        int ingredientCount = 0;
        List<ItemStack> inputs = new ArrayList<>();

        final int size = inv.getSizeInventory();
        for (int i = 0; i < size; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                ++ingredientCount;
                inputs.add(itemstack);
            }
        }

        if (ingredientCount != this.recipeItems.size())
            return false;

        int[] results = RecipeMatcher.findMatches(inputs, this.recipeItems);
        if (results == null) {
            return false;
        }
        lastResult = results;
        lastInv = inv;
        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.recipeOutput.copy();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width * height >= this.recipeItems.size();
    }

    public static final class Factory implements IRecipeFactory {

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
            Game.log(Level.INFO, "Remaining item shaped recipe factory loaded");
        }

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            // copied from forge's code for making shaped recipes
            String group = JsonUtils.getString(json, "group", "");

            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
                ingredients.add(CraftingHelper.getIngredient(ele, context));

            if (ingredients.isEmpty())
                throw new JsonParseException("No ingredients for shapeless recipe");
            if (ingredients.size() > 9)
                throw new JsonParseException("Too many ingredients for shapeless recipe");

            ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            // railcraft: changed the output
            return new RemainingItemShapelessRecipe(group, itemstack, ingredients);
        }
    }

}
