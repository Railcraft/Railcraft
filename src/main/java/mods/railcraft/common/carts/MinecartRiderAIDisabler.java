/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
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
public class MinecartRiderAIDisabler {

    @SubscribeEvent
    public void entityTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (!entity.worldObj.isRemote) {
                if (entity.ticksExisted % 5 == 1) { // one tick later than vanilla handle...
                    boolean notRiddenByAiMob = !(living.getControllingPassenger() instanceof EntityLiving);
                    Entity riding = entity.getRidingEntity();
                    boolean notRidingVehicle = !(riding instanceof EntityMinecart) && !(riding instanceof EntityBoat);
                    living.tasks.setControlFlag(4, notRiddenByAiMob && notRidingVehicle);
                }
            }
        }
    }
}
