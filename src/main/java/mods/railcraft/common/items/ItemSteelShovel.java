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
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.misc.MiscTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemSteelShovel extends ItemSpade {

    public ItemSteelShovel() {
        super(ItemMaterials.STEEL_TOOL);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setUnlocalizedName("railcraft.tool.steel.shovel");
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
