/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCrowbarSteel extends ItemCrowbar {

    public ItemCrowbarSteel() {
        super(ItemMaterials.Material.STEEL, ItemMaterials.STEEL_TOOL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                " RI",
                "RIR",
                "IR ",
                'I', "ingotSteel",
                'R', "dyeRed");
    }

}
