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
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.misc.MiscTools;

public class ItemSteelHoe extends ItemHoe {

    public ItemSteelHoe() {
        super(ItemMaterials.STEEL_TOOL);
        setUnlocalizedName("railcraft.tool.steel.hoe");
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
}
