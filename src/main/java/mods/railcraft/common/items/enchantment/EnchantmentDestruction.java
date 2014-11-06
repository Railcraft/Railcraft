package mods.railcraft.common.items.enchantment;

public class EnchantmentDestruction extends EnchantmentCrowbar {

    public EnchantmentDestruction(int id, int weight) {
        super("destruction", id, weight);
    }

    @Override
    public int getMinEnchantability(int level) {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}
