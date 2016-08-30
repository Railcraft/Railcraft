/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class ItemRailcraftArmor extends ItemArmor implements IRailcraftItem {
    protected ItemRailcraftArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item getObject() {
        return this;
    }
}
