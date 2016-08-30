/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

public class ItemSteelHoe extends ItemHoe implements IRailcraftItem {

    public ItemSteelHoe() {
        super(ItemMaterials.STEEL_TOOL);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item getObject() {
        return this;
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.HOE_STEEL, 1, 1, LootPlugin.Type.TOOL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), false,
                "II ",
                " S ",
                " S ",
                'I', "ingotSteel",
                'S', "stickWood");
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }
}
