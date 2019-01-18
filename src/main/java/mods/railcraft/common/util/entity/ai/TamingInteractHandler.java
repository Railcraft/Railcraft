/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.UUID;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TamingInteractHandler {

    private final Random rand = new Random();

    //TODO: test
    @SubscribeEvent
    public void interact(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();

        if (entity instanceof EntityTameable) {
            EntityTameable tamable = (EntityTameable) entity;
            UUID ownerId = tamable.getOwnerId();
            if (tamable.isTamed() && ownerId == null) {
                if (rand.nextInt(3) == 0) {
                    tamable.setOwnerId(event.getEntityPlayer().getUniqueID());
                    playTameEffect(tamable, true);
                    tamable.getAISit().setSitting(true);
                    tamable.world.setEntityState(tamable, (byte) 7);
                } else {
                    playTameEffect(tamable, false);
                    tamable.world.setEntityState(tamable, (byte) 6);
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
            tamable.world.spawnParticle(particleType, tamable.posX + (double) (rand.nextFloat() * tamable.width * 2.0F) - (double) tamable.width, tamable.posY + 0.5D + (double) (rand.nextFloat() * tamable.height), tamable.posZ + (double) (rand.nextFloat() * tamable.width * 2.0F) - (double) tamable.width, var4, var6, var8);
        }
    }
}
