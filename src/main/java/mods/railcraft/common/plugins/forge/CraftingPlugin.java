/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.google.common.collect.Lists;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.util.crafting.InvalidRecipeException;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CraftingPlugin {

    public static void addFurnaceRecipe(@Nullable ItemStack input, @Nullable ItemStack output, float xp) {
        if (isEmpty(input) && isEmpty(output)) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe, the input and output were both null. Skipping");
            return;
        }
        if (isEmpty(input)) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the input was null. Skipping", output.getUnlocalizedName());
            return;
        }
        if (isEmpty(output)) {
            Game.logTrace(Level.WARN, "Tried to define invalid furnace recipe for {0}, the output was null. Skipping", input.getUnlocalizedName());
            return;
        }
        FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }

    public static Object[] cleanRecipeArray(RecipeType recipeType, ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        List<Object> recipeList = Lists.newArrayList(recipeArray);
        for (int i = 0; i < recipeList.size(); i++) {
            Object obj = recipeList.get(i);
            if (obj instanceof IRailcraftRecipeIngredient) {
                Object obj2 = i + 1 < recipeList.size() ? recipeList.get(i + 1) : null;
                if (obj2 instanceof IVariantEnum) {
                    recipeList.set(i, ((IRailcraftRecipeIngredient) obj).getRecipeObject((IVariantEnum) obj2));
                    recipeList.remove(i + 1);
                } else {
                    recipeList.set(i, ((IRailcraftRecipeIngredient) obj).getRecipeObject());
                }
                if (recipeList.get(i) == null)
                    throw new MissingIngredientException(recipeType, result);
            } else if (obj == null) {
                throw new MissingIngredientException(recipeType, result);
            }
        }
        return recipeList.toArray();
    }

    private static boolean isOreRecipe(RecipeType recipeType, ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        for (Object obj : recipeArray) {
            if (obj instanceof String) {
                if (recipeType == RecipeType.SHAPELESS || ((String) obj).length() > 3)
                    return true;
            } else if (recipeType == RecipeType.SHAPED && obj instanceof Boolean)
                return true;
            else if (obj == null) {
                throw new MissingIngredientException(recipeType, result);
            }
        }
        return false;
    }

    public static ProcessedRecipe processRecipe(RecipeType recipeType, @Nullable ItemStack result, Object... recipeArray) throws InvalidRecipeException {
        if (isEmpty(result)) {
            throw new InvalidRecipeException("Tried to define invalid {0} recipe, the result was null or zero. Skipping", recipeType);
        }
        recipeArray = cleanRecipeArray(recipeType, result, recipeArray);
        boolean isOreRecipe = isOreRecipe(recipeType, result, recipeArray);
        return new ProcessedRecipe(isOreRecipe, result, recipeArray);
    }

    public static void addRecipe(@Nullable ItemStack result, Object... recipeArray) {
        ProcessedRecipe processedRecipe;
        try {
            processedRecipe = processRecipe(RecipeType.SHAPED, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.isOreRecipe) {
            IRecipe recipe = new ShapedOreRecipe(processedRecipe.result, processedRecipe.recipeArray);
            addRecipe(recipe);
        } else
            GameRegistry.addRecipe(processedRecipe.result, processedRecipe.recipeArray);
    }

    public static void addShapelessRecipe(@Nullable ItemStack result, Object... recipeArray) {
        ProcessedRecipe processedRecipe;
        try {
            processedRecipe = processRecipe(RecipeType.SHAPELESS, result, recipeArray);
        } catch (InvalidRecipeException ex) {
            Game.logTrace(Level.WARN, ex.getRawMessage());
            return;
        }
        if (processedRecipe.isOreRecipe) {
            IRecipe recipe = new ShapelessOreRecipe(processedRecipe.result, processedRecipe.recipeArray);
            addRecipe(recipe);
        } else
            GameRegistry.addShapelessRecipe(processedRecipe.result, processedRecipe.recipeArray);
    }

    public static void addRecipe(IRecipe recipe) {
        GameRegistry.addRecipe(recipe);
    }

    public static IRecipe makeVanillaShapedRecipe(ItemStack output, Object... components) {
        String s = "";
        int index = 0;
        int width = 0;
        int height = 0;
        if (components[index] instanceof String[]) {
            String as[] = (String[]) components[index++];
            for (String s2 : as) {
                height++;
                width = s2.length();
                s = (new StringBuilder()).append(s).append(s2).toString();
            }
        } else {
            while (components[index] instanceof String) {
                String s1 = (String) components[index++];
                height++;
                width = s1.length();
                s = (new StringBuilder()).append(s).append(s1).toString();
            }
        }
        HashMap<Character, ItemStack> hashMap = new HashMap<Character, ItemStack>();
        for (; index < components.length; index += 2) {
            Character character = (Character) components[index];
            ItemStack itemStack = InvTools.emptyStack();
            if (components[index + 1] instanceof Item) {
                itemStack = new ItemStack((Item) components[index + 1]);
            } else if (components[index + 1] instanceof Block) {
                itemStack = new ItemStack((Block) components[index + 1], 1, -1);
            } else if (components[index + 1] instanceof ItemStack) {
                itemStack = (ItemStack) components[index + 1];
            }
            hashMap.put(character, itemStack);
        }

        ItemStack recipeArray[] = new ItemStack[width * height];
        for (int i1 = 0; i1 < width * height; i1++) {
            char c = s.charAt(i1);
            if (hashMap.containsKey(c)) {
                recipeArray[i1] = hashMap.get(c).copy();
            } else {
                recipeArray[i1] = InvTools.emptyStack();
            }
        }

        return new ShapedRecipes(width, height, recipeArray, output);
    }

    public static IRecipe makeVanillaShapelessRecipe(ItemStack output, Object... components) {
        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        for (Object obj : components) {
            if (obj instanceof ItemStack) {
                ingredients.add(((ItemStack) obj).copy());
                continue;
            }
            if (obj instanceof Item) {
                ingredients.add(new ItemStack((Item) obj));
                continue;
            }
            if (obj instanceof Block) {
                ingredients.add(new ItemStack((Block) obj));
            }
        }

        return new ShapelessRecipes(output, ingredients);
    }

    public static ItemStack[] emptyContainers(InventoryCrafting inv) {
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < grid.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            grid[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return grid;
    }

    @Nullable
    public static ItemStack getIngredientStack(IRailcraftRecipeIngredient ingredient, int qty) {
        Object object = ingredient.getRecipeObject();
        if (object instanceof ItemStack) {
            ItemStack stack = ((ItemStack) object).copy();
            stack.stackSize = qty;
            return stack;
        }
        if (object instanceof Item)
            return new ItemStack((Item) object, qty);
        if (object instanceof Block)
            return new ItemStack((Block) object, qty);
        if (object instanceof String)
            return OreDictPlugin.getOre((String) object, qty);
        throw new RuntimeException("Unknown ingredient object");
    }

    public enum RecipeType {
        SHAPED, SHAPELESS
    }

    private static class MissingIngredientException extends InvalidRecipeException {
        public MissingIngredientException(RecipeType recipeType, ItemStack result) {
            super("Tried to define invalid {0} recipe for {1}, a necessary item was probably disabled. Skipping", recipeType, result.getUnlocalizedName());
        }
    }

    public static class ProcessedRecipe {
        public final ItemStack result;
        public final Object[] recipeArray;
        public final boolean isOreRecipe;

        ProcessedRecipe(boolean isOreRecipe, ItemStack result, Object... recipeArray) {
            this.isOreRecipe = isOreRecipe;
            this.result = result;
            this.recipeArray = recipeArray;
        }
    }

}
