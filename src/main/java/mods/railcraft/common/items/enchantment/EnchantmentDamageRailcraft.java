package mods.railcraft.common.items.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.ref.WeakReference;

public class EnchantmentDamageRailcraft extends EnchantmentCrowbar {

    private final int baseEnchantability, levelEnchantability, thresholdEnchantability;
    private final Class<? extends EntityLivingBase> targetType;
    private final float damageBonusPerLevel;
    private WeakReference<Entity> target;

    public EnchantmentDamageRailcraft(String tag, Rarity rarity, int baseEnchantability, int levelEnchantability, int thresholdEnchantability, Class<? extends EntityLivingBase> targetType, float damageBonusPerLevel) {
        super(tag, rarity, EntityEquipmentSlot.MAINHAND);
        this.baseEnchantability = baseEnchantability;
        this.levelEnchantability = levelEnchantability;
        this.thresholdEnchantability = thresholdEnchantability;
        this.targetType = targetType;
        this.damageBonusPerLevel = damageBonusPerLevel;
    }

    @Override
    public int getMinEnchantability(int level) {
        return baseEnchantability + (level - 1) * levelEnchantability;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + thresholdEnchantability;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float calcDamageByCreature(int lvl, EnumCreatureAttribute creatureType) {
        float modifier = 0.0f;
        if (targetType == null || (target != null && targetType.isInstance(target.get())))
            modifier = lvl * damageBonusPerLevel;
        target = null;
        return modifier;
    }

    @SubscribeEvent
    public void attackEvent(AttackEntityEvent event) {
        target = new WeakReference<Entity>(event.getTarget());
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamageRailcraft);
    }

}
