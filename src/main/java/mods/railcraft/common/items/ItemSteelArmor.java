/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
// FIXME IC2 compat
public class ItemSteelArmor extends ItemArmor/* implements IMetalArmor*/ {

    private static final String TEXTURE_1 = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "steel_1.png";
    private static final String TEXTURE_2 = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "steel_2.png";

    public ItemSteelArmor(int type) {
        super(ItemMaterials.STEEL_ARMOR, 0, type);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (armorType == 2) {
            return TEXTURE_2;
        }
        return TEXTURE_1;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return OreDictPlugin.isOreType("ingotSteel", stack);
    }

    // FIXME IC2 compat
//    @Override
//    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
//        return true;
//    }

}
