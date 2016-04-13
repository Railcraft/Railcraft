/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCrowbarIron extends ItemCrowbar {

    private static final String ITEM_TAG = "railcraft.tool.crowbar.iron";

    public ItemCrowbarIron() {
        super(ToolMaterial.IRON);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                " RI",
                "RIR",
                "IR ",
                'I', "ingotIron",
                'R', "dyeRed");
    }

    @Override
    public void initializeDefinintion() {
        super.initializeDefinintion();
        LootPlugin.addLoot(new ItemStack(this), 1, 1, LootPlugin.Type.TOOL, ITEM_TAG);
        LootPlugin.addLoot(new ItemStack(this), 1, 1, LootPlugin.Type.WORKSHOP, ITEM_TAG);
    }

}
