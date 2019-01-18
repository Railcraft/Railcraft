/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This is a hack to disable entity AI while riding minecarts.
 * It is based off code for Boats found in EntityLiving#onUpdate.
 *
 * Created by CovertJaguar on 10/5/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO Patch EntityMinecart class moveAlongTrack method in forge
public class MinecartRiderAIDisabler {

    @SubscribeEvent
    public void entityTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (!entity.world.isRemote) {
                if (entity.ticksExisted % 8 == 0) {
                    boolean ridingMinecart = entity.getRidingEntity() instanceof EntityMinecart;
                    // 4 is the boat field
                    living.tasks.setControlFlag(4, !ridingMinecart);
                }
            }
        }
    }
}
