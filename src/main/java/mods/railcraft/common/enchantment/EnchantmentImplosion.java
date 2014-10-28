package mods.railcraft.common.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.DamageSource;

public class EnchantmentImplosion extends EnchantmentCrowbar {

    public EnchantmentImplosion(int id, int weight) {
        super(id, weight);
        this.setName("implosion");
    }

    public int getMinEnchantability(int level) {
        return 20;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 10;
    }

    public int getMaxLevel() {
        return 1;
    }

    public void func_151368_a(EntityLivingBase p_151368_1_, Entity p_151368_2_, int level) {
        EntityLivingBase entity = (EntityLivingBase)p_151368_2_;
        if (entity instanceof EntityCreeper) {
            entity.attackEntityFrom(DamageSource.generic, entity.getMaxHealth());
        }
    }
}
