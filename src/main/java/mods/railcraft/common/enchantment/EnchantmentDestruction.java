package mods.railcraft.common.enchantment;

public class EnchantmentDestruction extends EnchantmentCrowbar {

    public EnchantmentDestruction(int id, int weight) {
        super(id, weight);
        this.setName("destruction");
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
}
