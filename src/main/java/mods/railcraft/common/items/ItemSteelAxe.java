/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemSteelAxe extends ItemAxe implements IRailcraftItem {

    public ItemSteelAxe() {
        super(ItemMaterials.STEEL_TOOL, 8F, -3F);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item getObject() {
        return this;
    }

    @Override
    public void initializeDefinintion() {
        HarvestPlugin.setToolClass(this, "axe", 2);
        LootPlugin.addLoot(RailcraftItems.AXE_STEEL, 1, 1, LootPlugin.Type.TOOL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this), false,
                "II ",
                "IS ",
                " S ",
                'I', "ingotSteel",
                'S', "stickWood");
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (state.getBlock() == Blocks.MELON_BLOCK)
            return efficiencyOnProperMaterial;
        if (state.getMaterial() == Material.LEAVES)
            return efficiencyOnProperMaterial;
        return super.getStrVsBlock(stack, state);
    }

}
