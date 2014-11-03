package mods.railcraft.common.enchantment;

import net.minecraft.entity.EnumCreatureAttribute;

public class EnchantmentWrecking extends EnchantmentCrowbar {

    public EnchantmentWrecking(int id, int weight) {
        super(id, weight);
        this.setName("wrecking");
    }

    public int getMinEnchantability(int level) {
        return 5 + (level - 1) * 10;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 10;
    }

    public int getMaxLevel() {
        return 3;
    }

    public float func_152376_a(int level, EnumCreatureAttribute p_152376_2_)
    {
        return (float)level * 1.5F;
    }
}
