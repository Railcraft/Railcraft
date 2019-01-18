/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemRebar extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        Crafters.rollingMachine().newRecipe(new ItemStack(this, 4)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', new ItemStack(Items.IRON_INGOT));

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 4)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotBronze");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 6)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotRefinedIron");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 6)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotInvar");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 8)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotSteel");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 8)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotDarkSteel");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 16)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotTungsten");

        Crafters.rollingMachine().newRecipe(new ItemStack(this, 16)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "ingotTitanium");

    }

}
