/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemRebar extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        RailcraftCraftingManager.rollingMachine().addRecipe(new ItemStack(this, 4),
                "  I",
                " I ",
                "I  ",
                'I', new ItemStack(Items.IRON_INGOT));

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 4),
                "  I",
                " I ",
                "I  ",
                'I', "ingotBronze");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 6),
                "  I",
                " I ",
                "I  ",
                'I', "ingotRefinedIron");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 6),
                "  I",
                " I ",
                "I  ",
                'I', "ingotInvar");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 8),
                "  I",
                " I ",
                "I  ",
                'I', "ingotSteel");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 8),
                "  I",
                " I ",
                "I  ",
                'I', "ingotDarkSteel");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 16),
                "  I",
                " I ",
                "I  ",
                'I', "ingotTungsten");

        RollingMachineCraftingManager.getInstance().addRecipe(new ItemStack(this, 16),
                "  I",
                " I ",
                "I  ",
                'I', "ingotTitanium");

    }

}
