/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.IDirectionalCart;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.modules.ModuleTrain;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public class CrowbarHandler {
    public static final float SMACK_VELOCITY = 0.07f;
    private static final Map<EntityPlayer, EntityMinecart> linkMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static CrowbarHandler instance;

    public static CrowbarHandler instance() {
        if (instance == null)
            instance = new CrowbarHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(MinecartInteractEvent event) {
        EntityPlayer thePlayer = event.getPlayer();
        Entity entity = event.getEntity();
        EnumHand hand = event.getHand();

        if (event.getItem() != null && event.getItem().getItem() instanceof IToolCrowbar)
            event.setCanceled(true);

        ItemStack stack = event.getItem();
        if (stack != null && stack.getItem() instanceof IToolCrowbar) {
            thePlayer.swingArm(event.getHand());
            event.setCanceled(true);
        } else
            return;

        if (Game.isClient(thePlayer.world))
            return;

        boolean used = false;
        IToolCrowbar crowbar = (IToolCrowbar) stack.getItem();
        if (entity instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) entity;

            if (RailcraftModuleManager.isModuleEnabled(ModuleTrain.class)
                    && crowbar.canLink(thePlayer, hand, stack, cart)) {
                boolean linkable = cart instanceof ILinkableCart;
                if (!linkable || ((ILinkableCart) cart).isLinkable()) {
                    EntityMinecart last = linkMap.remove(thePlayer);
                    if (last != null && !last.isDead) {
                        LinkageManager lm = LinkageManager.instance();
                        if (lm.areLinked(cart, last, false)) {
                            lm.breakLink(cart, last);
                            used = true;
                            ChatPlugin.sendLocalizedChatFromServer(thePlayer, "railcraft.gui.link.broken");
                            LinkageManager.printDebug("Reason For Broken Link: User removed link.");
                        } else {
                            used = lm.createLink(last, (EntityMinecart) entity);
                            if (used)
                                ChatPlugin.sendLocalizedChatFromServer(thePlayer, "railcraft.gui.link.created");
                        }
                        if (!used)
                            ChatPlugin.sendLocalizedChatFromServer(thePlayer, "railcraft.gui.link.failed");
                    } else {
                        linkMap.put(thePlayer, (EntityMinecart) entity);
                        ChatPlugin.sendLocalizedChatFromServer(thePlayer, "railcraft.gui.link.started");
                    }
                }
                if (used)
                    crowbar.onLink(thePlayer, hand, stack, cart);
            } else if (crowbar.canBoost(thePlayer, hand, stack, cart)) {
                thePlayer.addExhaustion(1F);

                //noinspection StatementWithEmptyBody
                if (thePlayer.getRidingEntity() != null) {
                    // NOOP
                } else //noinspection StatementWithEmptyBody
                    if (cart instanceof EntityTunnelBore) {
                        // NOOP
                    } else if (cart instanceof IDirectionalCart)
                        ((IDirectionalCart) cart).reverse();
                    else {
                        if (cart.posX < thePlayer.posX)
                            cart.motionX -= SMACK_VELOCITY;
                        else
                            cart.motionX += SMACK_VELOCITY;
                        if (cart.posZ < thePlayer.posZ)
                            cart.motionZ -= SMACK_VELOCITY;
                        else
                            cart.motionZ += SMACK_VELOCITY;
                    }
                crowbar.onBoost(thePlayer, hand, stack, cart);
            }
        }
    }
}
