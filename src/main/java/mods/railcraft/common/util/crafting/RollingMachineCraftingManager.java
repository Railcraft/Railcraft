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
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import mods.railcraft.api.crafting.IRollingMachineCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;

public class RollingMachineCraftingManager implements IRollingMachineCraftingManager
{

    private final List<IRecipe> recipes = new ArrayList<IRecipe>();

    public static IRollingMachineCraftingManager getInstance() {
        return RailcraftCraftingManager.rollingMachine;
    }

    public static void copyRecipesToWorkbench() {
        CraftingManager.getInstance().getRecipeList().addAll(getInstance().getRecipeList());
    }

    @Override
    public void addRecipe(ItemStack output, Object... components) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        if(components[i] instanceof String[]) {
            String as[] = (String[])components[i++];
            for(int l = 0; l < as.length; l++) {
                String s2 = as[l];
                k++;
                j = s2.length();
                s = (new StringBuilder()).append(s).append(s2).toString();
            }
        } else {
            while(components[i] instanceof String) {
                String s1 = (String)components[i++];
                k++;
                j = s1.length();
                s = (new StringBuilder()).append(s).append(s1).toString();
            }
        }
        HashMap hashmap = new HashMap();
        for(; i < components.length; i += 2) {
            Character character = (Character)components[i];
            ItemStack itemstack1 = null;
            if(components[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item)components[i + 1]);
            } else if(components[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block)components[i + 1], 1, -1);
            } else if(components[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack)components[i + 1];
            }
            hashmap.put(character, itemstack1);
        }

        ItemStack recipeArray[] = new ItemStack[j * k];
        for(int i1 = 0; i1 < j * k; i1++) {
            char c = s.charAt(i1);
            if(hashmap.containsKey(Character.valueOf(c))) {
                recipeArray[i1] = ((ItemStack)hashmap.get(Character.valueOf(c))).copy();
            } else {
                recipeArray[i1] = null;
            }
        }

        recipes.add(new ShapedRecipes(j, k, recipeArray, output));
    }

    @Override
    public void addShapelessRecipe(ItemStack output, Object... components) {
        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        for(int j = 0; j < components.length; j++) {
            Object obj = components[j];
            if(obj instanceof ItemStack) {
                ingredients.add(((ItemStack)obj).copy());
                continue;
            }
            if(obj instanceof Item) {
                ingredients.add(new ItemStack((Item)obj));
                continue;
            }
            if(obj instanceof Block) {
                ingredients.add(new ItemStack((Block)obj));
            } else {
                throw new RuntimeException("Invalid shapeless recipe!");
            }
        }

        recipes.add(new ShapelessRecipes(output, ingredients));
    }

    @Override
    public ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {
        for(int k = 0; k < recipes.size(); k++) {
            IRecipe irecipe = (IRecipe)recipes.get(k);
            if(irecipe.matches(inv, world)) {
                return irecipe.getCraftingResult(inv);
            }
        }

        return null;
    }

    @Override
    public List<IRecipe> getRecipeList() {
        return recipes;
    }
}
