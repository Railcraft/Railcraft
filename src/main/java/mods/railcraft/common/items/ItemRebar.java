/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemRebar extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        Crafters.rollingMachine().addRecipe(new ItemStack(this, 4),
                "  I",
                " I ",
                "I  ",
                'I', new ItemStack(Items.IRON_INGOT));

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 4),
                "  I",
                " I ",
                "I  ",
                'I', "ingotBronze");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 6),
                "  I",
                " I ",
                "I  ",
                'I', "ingotRefinedIron");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 6),
                "  I",
                " I ",
                "I  ",
                'I', "ingotInvar");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 8),
                "  I",
                " I ",
                "I  ",
                'I', "ingotSteel");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 8),
                "  I",
                " I ",
                "I  ",
                'I', "ingotDarkSteel");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 16),
                "  I",
                " I ",
                "I  ",
                'I', "ingotTungsten");

        Crafters.rollingMachine().addRecipe(new ItemStack(this, 16),
                "  I",
                " I ",
                "I  ",
                'I', "ingotTitanium");

    }

}
