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
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCoke extends ItemRailcraft {
    private static final int COKE_COOK_CREOSOTE = 500;
    public static final int COKE_HEAT = 3200;

    @Override
    public void initializeDefinition() {
        OreDictionary.registerOre("fuelCoke", new ItemStack(this));
    }

    @Override
    public void defineRecipes() {
        if (RailcraftConfig.coalCokeTorchOutput() > 0) {
            CraftingPlugin.addShapedRecipe(new ItemStack(Blocks.TORCH, RailcraftConfig.coalCokeTorchOutput()),
                    "C",
                    "S",
                    'C', "fuelCoke",
                    'S', "stickWood");
        }
        Crafters.cokeOven().newRecipe(Ingredients.from(Items.COAL, 0))
                .name("railcraft:coke")
                .output(getStack())
                .fluid(Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE))
                .register();
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return COKE_HEAT;
    }
}
