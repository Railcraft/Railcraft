package mods.railcraft.common.items.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentDestruction extends EnchantmentCrowbar {

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

}
