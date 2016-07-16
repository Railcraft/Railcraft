/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.plugins.forge.*;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ItemSteelPickaxe extends ItemPickaxe implements IRailcraftObject {

    public ItemSteelPickaxe() {
        super(ItemMaterials.STEEL_TOOL);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void initializeDefinintion() {
        HarvestPlugin.setToolClass(this, "pickaxe", 2);
        LootPlugin.addLoot(RailcraftItems.pickaxeSteel, 1, 1, LootPlugin.Type.TOOL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), false,
                "III",
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
