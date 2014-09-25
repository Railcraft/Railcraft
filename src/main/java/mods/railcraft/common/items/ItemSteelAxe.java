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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemSteelAxe extends ItemAxe {

    public ItemSteelAxe() {
        super(ItemMaterials.STEEL_TOOL);
        setUnlocalizedName("railcraft.tool.steel.axe");
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(getUnlocalizedName()));
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (block == Blocks.melon_block)
            return efficiencyOnProperMaterial;
        if (block.getMaterial() == Material.leaves)
            return efficiencyOnProperMaterial;
        return super.getDigSpeed(stack, block, meta);
    }

}
