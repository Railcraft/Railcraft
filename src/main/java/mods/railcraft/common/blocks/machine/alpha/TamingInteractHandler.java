/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TamingInteractHandler {

    private Random rand = new Random();

    @SubscribeEvent
    public void interact(EntityInteractEvent event) {
        Entity entity = event.target;

        if (entity instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable) entity;
            String ownerId = tameable.func_152113_b();
            if (tameable.isTamed() && (ownerId == null || ownerId.trim().length() == 0)) {
                if (rand.nextInt(3) == 0) {
                    tameable.func_152115_b(event.entityPlayer.getUniqueID().toString());
                    playTameEffect(tameable, true);
                    tameable.func_70907_r().setSitting(true);
                    tameable.worldObj.setEntityState(tameable, (byte) 7);
                } else {
                    playTameEffect(tameable, false);
                    tameable.worldObj.setEntityState(tameable, (byte) 6);
                }
                event.setCanceled(true);
            }
        }
    }

    protected void playTameEffect(EntityTameable tameable, boolean par1) {
        String var2 = "heart";

        if (!par1)
            var2 = "smoke";

        for (int var3 = 0; var3 < 7; ++var3) {
            double var4 = rand.nextGaussian() * 0.02D;
            double var6 = rand.nextGaussian() * 0.02D;
            double var8 = rand.nextGaussian() * 0.02D;
            tameable.worldObj.spawnParticle(var2, tameable.posX + (double) (rand.nextFloat() * tameable.width * 2.0F) - (double) tameable.width, tameable.posY + 0.5D + (double) (rand.nextFloat() * tameable.height), tameable.posZ + (double) (rand.nextFloat() * tameable.width * 2.0F) - (double) tameable.width, var4, var6, var8);
        }
    }

}
