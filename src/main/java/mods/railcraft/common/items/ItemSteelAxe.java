/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemSteelAxe extends ItemAxe {

    public ItemSteelAxe() {
        super(ItemMaterials.STEEL_TOOL);
        setUnlocalizedName("railcraft.tool.steel.axe");
        setRegistryName("Railcraft:tool.steel.axe");
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    @Override
    public float getDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        if (state.getBlock() == Blocks.melon_block)
            return efficiencyOnProperMaterial;
        if (state.getBlock().getMaterial() == Material.leaves)
            return efficiencyOnProperMaterial;
        return super.getDigSpeed(stack, state);
    }

}
