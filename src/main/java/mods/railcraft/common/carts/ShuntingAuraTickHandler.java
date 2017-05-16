/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.forge.EntitySearcher;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketShuntingAura;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ShuntingAuraTickHandler {

    private int clock;

    @SubscribeEvent
    public void tick(PlayerEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (Game.isClient(entity.worldObj))
            return;
        clock++;
        if (clock % 16 != 0)
            return;
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (ItemGoggles.isPlayerWearing(player)) {
                ItemStack goggles = ItemGoggles.getGoggles(player);
                ItemGoggles.GoggleAura aura = ItemGoggles.getCurrentAura(goggles);
                if (aura == ItemGoggles.GoggleAura.SHUNTING) {
                    List<EntityMinecart> carts = EntitySearcher.findMinecarts().around(player, 32F).at(player.worldObj);
                    PacketShuntingAura pkt = new PacketShuntingAura(carts);
                    PacketDispatcher.sendToPlayer(pkt.getPacket(), player);
                }
            }
        }
    }

}
