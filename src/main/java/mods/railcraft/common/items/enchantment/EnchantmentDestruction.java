/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.enchantment;

import mods.railcraft.common.items.ItemCrowbar;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentDestruction extends EnchantmentToolRailcraft {

    public EnchantmentDestruction(Rarity rarity) {
        super("destruction", rarity, EntityEquipmentSlot.MAINHAND);
    }

    @Override
    public int getMinEnchantability(int level) {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemCrowbar;
    }

}
