/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TamingInteractHandler {

    private Random rand = new Random();

    @SubscribeEvent
    public void interact(EntityInteractEvent event) {
        Entity entity = event.target;

        if (entity instanceof EntityTameable) {
            EntityTameable tamable = (EntityTameable) entity;
            String ownerId = tamable.getOwnerId();
            if (tamable.isTamed() && (ownerId == null || ownerId.trim().length() == 0)) {
                if (rand.nextInt(3) == 0) {
                    tamable.setOwnerId(event.entityPlayer.getUniqueID().toString());
                    playTameEffect(tamable, true);
                    tamable.getAISit().setSitting(true);
                    tamable.worldObj.setEntityState(tamable, (byte) 7);
                } else {
                    playTameEffect(tamable, false);
                    tamable.worldObj.setEntityState(tamable, (byte) 6);
                }
                event.setCanceled(true);
            }
        }
    }

    protected void playTameEffect(EntityTameable tamable, boolean par1) {
        EnumParticleTypes particleType = EnumParticleTypes.HEART;

        if (!par1)
            particleType = EnumParticleTypes.SMOKE_NORMAL;

        for (int var3 = 0; var3 < 7; ++var3) {
            double var4 = rand.nextGaussian() * 0.02D;
            double var6 = rand.nextGaussian() * 0.02D;
            double var8 = rand.nextGaussian() * 0.02D;
            tamable.worldObj.spawnParticle(particleType, tamable.posX + (double) (rand.nextFloat() * tamable.width * 2.0F) - (double) tamable.width, tamable.posY + 0.5D + (double) (rand.nextFloat() * tamable.height), tamable.posZ + (double) (rand.nextFloat() * tamable.width * 2.0F) - (double) tamable.width, var4, var6, var8);
        }
    }
}
