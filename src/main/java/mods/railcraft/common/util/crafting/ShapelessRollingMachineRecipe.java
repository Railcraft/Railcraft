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
import net.minecraftforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ShapelessRollingMachineRecipe implements IRollingMachineRecipe {
    private final List<Ingredient> ingredients;
    private final ItemStack output;

    private final int time;

    ShapelessRollingMachineRecipe(List<Ingredient> items, ItemStack output, int time) {
        this.ingredients = items;
        this.output = output;
        this.time = time;
    }

    @Override
    public boolean test(InventoryCrafting inv) {
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
