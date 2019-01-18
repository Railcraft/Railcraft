/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.enchantment;

import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public class EnchantmentDamageRailcraft extends EnchantmentToolRailcraft {

    private final int baseEnchantability, levelEnchantability, thresholdEnchantability;
    private final Predicate<? super Entity> check;
    private final float damageBonusPerLevel;
    private WeakReference<Entity> target;

    public EnchantmentDamageRailcraft(String tag, Rarity rarity, int baseEnchantability, int levelEnchantability, int thresholdEnchantability, @Nullable Predicate<? super Entity> check, float damageBonusPerLevel) {
        super(tag, rarity, EntityEquipmentSlot.MAINHAND);
        this.baseEnchantability = baseEnchantability;
        this.levelEnchantability = levelEnchantability;
        this.thresholdEnchantability = thresholdEnchantability;
        this.check = check == null ? Predicates.alwaysTrue() : check;
        this.damageBonusPerLevel = damageBonusPerLevel;
        MinecraftForge.EVENT_BUS.register(this);
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
        if ((target != null && check.test(target.get())))
            modifier = lvl * damageBonusPerLevel;
        target = null;
        return modifier;
    }

    @SubscribeEvent
    public void attackEvent(AttackEntityEvent event) {
        target = new WeakReference<>(event.getTarget());
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamageRailcraft) && !(enchantment instanceof EnchantmentDamage);
    }

}
