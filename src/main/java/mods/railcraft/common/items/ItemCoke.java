/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCoke extends ItemRailcraft {
    public static final int COKE_HEAT = 3200;

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.COKE, 4, 16, LootPlugin.Type.TOOL);
        LootPlugin.addLoot(RailcraftItems.COKE, 4, 16, LootPlugin.Type.WORKSHOP);

        OreDictionary.registerOre("fuelCoke", new ItemStack(this));
    }

    @Override
    public void defineRecipes() {
        if (RailcraftConfig.coalCokeTorchOutput() > 0) {
            CraftingPlugin.addRecipe(new ItemStack(Blocks.TORCH, RailcraftConfig.coalCokeTorchOutput()),
                    "C",
                    "S",
                    'C', new ItemStack(this),
                    'S', "stickWood");
        }
    }

    @Override
    public int getHeatValue(ItemStack stack) {
        return COKE_HEAT;
    }
}
