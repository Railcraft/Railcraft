/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A shaped recipe which respects its ingredient's choice of remaining items.
 */
@SuppressWarnings({"unused", "UnnecessaryThis"})
public final class ShapedRailcraftRecipe extends ShapedRecipes {

    // is there a cleaner way to map Ingredients to ItemStacks?
    @Nullable RailcraftIngredient[] bySlots;

    public ShapedRailcraftRecipe(String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(group, width, height, ingredients, result);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        if (bySlots == null || bySlots.length != inv.getSizeInventory()) {
            matches(inv, null);
        }

        requireNonNull(bySlots);

        NonNullList<ItemStack> nonNullList = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            RailcraftIngredient ingredient = bySlots[i];

            if (ingredient != null) {
                nonNullList.set(i, ingredient.getRemaining(itemstack));
            } else {
                nonNullList.set(i, ForgeHooks.getContainerItem(itemstack));
            }
        }

        return nonNullList;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting inv, @Nullable World worldIn) {
        bySlots = new RailcraftIngredient[inv.getSizeInventory()];
        return super.matches(inv, worldIn);
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    @Override
    protected boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
        requireNonNull(bySlots);
        Arrays.fill(bySlots, null);
        for (int i = 0; i < p_77573_1_.getWidth(); ++i) {
            for (int j = 0; j < p_77573_1_.getHeight(); ++j) {
                int k = i - p_77573_2_;
                int l = j - p_77573_3_;
                Ingredient ingredient = Ingredient.EMPTY;

                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    int index = p_77573_4_ ? this.recipeWidth - k - 1 + l * this.recipeWidth : k + l * this.recipeWidth;
                    ingredient = this.recipeItems.get(index);

                    if (ingredient instanceof RailcraftIngredient) {
                        bySlots[index] = (RailcraftIngredient) ingredient;
                    }
                }

                if (!ingredient.apply(p_77573_1_.getStackInRowAndColumn(i, j))) {
                    Arrays.fill(bySlots, null);
                    return false;
                }
            }
        }

        return true;
    }

    public static final class Factory implements IRecipeFactory {

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
            Game.log().msg(Level.INFO, "Remaining item shaped recipe factory loaded");
        }

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            // copied from forge's code for making shaped recipes
            String group = JsonUtils.getString(json, "group", "");
            //if (!group.isEmpty() && group.indexOf(':') == -1)
            //    group = context.getModId() + ":" + group;

            Map<Character, Ingredient> ingMap = Maps.newHashMap();
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
                if (entry.getKey().length() != 1)
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                if (" ".equals(entry.getKey()))
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

                ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
            }
            ingMap.put(' ', Ingredient.EMPTY);

            JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

            if (patternJ.size() == 0)
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            if (patternJ.size() > 3)
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");

            String[] pattern = new String[patternJ.size()];
            for (int x = 0; x < pattern.length; ++x) {
                String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
                if (line.length() > 3)
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                if (x > 0 && pattern[0].length() != line.length())
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                pattern[x] = line;
            }

            NonNullList<Ingredient> input = NonNullList.withSize(pattern[0].length() * pattern.length, Ingredient.EMPTY);
            Set<Character> keys = Sets.newHashSet(ingMap.keySet());
            keys.remove(' ');

            int x = 0;
            for (String line : pattern) {
                for (char chr : line.toCharArray()) {
                    Ingredient ing = ingMap.get(chr);
                    if (ing == null)
                        throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
                    input.set(x++, ing);
                    keys.remove(chr);
                }
            }

            if (!keys.isEmpty())
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            // railcraft: changed the output
            return new ShapedRailcraftRecipe(group, pattern[0].length(), pattern.length, input, result);
        }
    }

}
