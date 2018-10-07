package mods.railcraft.common.util.crafting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mods.railcraft.common.util.inventory.InvTools;
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
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A shaped recipe which respects its ingredient's choice of remaining items.
 */
@SuppressWarnings("unused")
public final class RemainingItemShapedRecipe extends ShapedRecipes {

    private final int recipeWidth;
    /**
     * How many vertical slots this recipe uses.
     */
    private final int recipeHeight;
    /**
     * Is a array of ItemStack that composes the recipe.
     */
    private final NonNullList<Ingredient> recipeItems;
    /**
     * Is the ItemStack that you get when craft the recipe.
     */
    private final ItemStack recipeOutput;
    private final String group;

    @Nullable IRemainderIngredient[] bySlots;

    public RemainingItemShapedRecipe(String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(group, width, height, ingredients, result);
        this.group = group;
        this.recipeWidth = width;
        this.recipeHeight = height;
        this.recipeItems = ingredients;
        this.recipeOutput = result;
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
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        if (bySlots == null || bySlots.length != inv.getSizeInventory()) {
            matches(inv, null);
        }

        requireNonNull(bySlots);

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            IRemainderIngredient ingredient = bySlots[i];

            if (ingredient != null) {
                nonnulllist.set(i, ingredient.getRemaining(itemstack));
            } else {
                nonnulllist.set(i, ForgeHooks.getContainerItem(itemstack));
            }
        }

        return nonnulllist;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting inv, @Nullable World worldIn) {
        bySlots = new IRemainderIngredient[inv.getSizeInventory()];
        for (int i = 0; i <= inv.getWidth() - this.recipeWidth; ++i) {
            for (int j = 0; j <= inv.getHeight() - this.recipeHeight; ++j) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
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

                    if (ingredient instanceof IRemainderIngredient) {
                        bySlots[index] = (IRemainderIngredient) ingredient;
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

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.getRecipeOutput().copy();
    }

    public int getRecipeWidth() {
        return this.recipeWidth;
    }

    public int getRecipeHeight() {
        return this.recipeHeight;
    }

    public static final class Factory implements IRecipeFactory {

        /**
         * Invoked by forge via reflection.
         */
        public Factory() {
            Game.log(Level.INFO, "Remaining item shaped recipe factory loaded");
        }

        @NotNull
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
            return new RemainingItemShapedRecipe(group, pattern[0].length(), pattern.length, input, result);
        }
    }

}
