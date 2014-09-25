/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemRebar extends ItemRailcraft {

    public ItemRebar() {
    }

    @Override
    public void defineRecipes() {
        RailcraftCraftingManager.rollingMachine.addRecipe(new ItemStack(this, 4),
                "  I",
                " I ",
                "I  ",
                'I', new ItemStack(Items.iron_ingot));

        IRecipe recipe = new ShapedOreRecipe(new ItemStack(this, 4), true,
                "  I",
                " I ",
                "I  ",
                'I', "ingotBronze");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(this, 6), true,
                "  I",
                " I ",
                "I  ",
                'I', "ingotRefinedIron");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(this, 8), true,
                "  I",
                " I ",
                "I  ",
                'I', "ingotSteel");
        RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

    }

}
