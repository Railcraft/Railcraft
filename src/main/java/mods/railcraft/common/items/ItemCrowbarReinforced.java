/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCrowbarReinforced extends ItemCrowbar {

    private static final String ITEM_TAG = "railcraft.tool.crowbar.reinforced";
    public static Item item;

    public static void registerItem() {
        if (item == null && RailcraftConfig.isItemEnabled(ITEM_TAG)) {
            item = new ItemCrowbarReinforced();
            item.setUnlocalizedName(ITEM_TAG);
            RailcraftRegistry.register(item);

            CraftingPlugin.addShapedRecipe(new ItemStack(item),
                    " RI",
                    "RIR",
                    "IR ",
                    'I', "ingotSteel",
                    'R', "dyeRed");

//                LootPlugin.addLootTool(new ItemStack(item), 1, 1, ITEM_TAG);
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public ItemCrowbarReinforced() {
        super(ItemMaterials.STEEL_TOOL);
    }

}
