/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FirestoneTickHandler {

    @SubscribeEvent
    public void tick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (Game.isClient(entity.world))
            return;
        if ((entity.world.getTotalWorldTime() + entity.getEntityId()) % 4 != 0)
            return;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).openContainer != ((EntityPlayer) entity).inventoryContainer)
            return;
        InventoryComposite.of(entity).streamStacks().forEach(stack -> FirestoneTools.trySpawnFire(entity.world, entity.getPosition(), stack));
    }

}
