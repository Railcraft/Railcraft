/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMaterials {

    public static ToolMaterial STEEL_TOOL = EnumHelper.addToolMaterial("RAILCRAFT_STEEL", 2, 500, 7, 2, 9);
    public static ArmorMaterial STEEL_ARMOR = EnumHelper.addArmorMaterial("RAILCRAFT_STEEL", 25, new int[]{2, 6, 5, 2}, 8);
    public static ArmorMaterial GOGGLES = EnumHelper.addArmorMaterial("RAILCRAFT_GOGGLES", 20, new int[]{1, 3, 2, 1}, 15);
    public static ArmorMaterial OVERALLS = EnumHelper.addArmorMaterial("RAILCRAFT_OVERALLS", 5, new int[]{1, 3, 2, 1}, 15);
}
