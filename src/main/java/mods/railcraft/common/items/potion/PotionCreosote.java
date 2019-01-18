/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items.potion;

import mods.railcraft.common.util.entity.RailcraftDamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The creosote potion.
 */
final class PotionCreosote extends PotionRailcraft {
    PotionCreosote() {
        super(false, 0xcca300);
        setIconIndex(0, 0);
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
            entityLivingBaseIn.attackEntityFrom(RailcraftDamageSource.CREOSOTE, (float) Math.pow(1.10D, amplifier));
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int t = 25 >> amplifier;
        return t == 0 || duration % t == 0;
    }

    @Override
    public void finalizeDefinition() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        if (event.getSource() instanceof EntityDamageSource) {
            EntityDamageSource source = (EntityDamageSource) event.getSource();
            if (source.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) source.getTrueSource();
                PotionEffect effect = event.getEntityLiving().getActivePotionEffect(PotionCreosote.this);
                if (effect != null && entity.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
                    entity.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration() / 2, effect.getAmplifier()));
                }
            }
        }
    }
}
