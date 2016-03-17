/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.IDirectionalCart;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

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
    public void onEntityInteract(EntityInteractEvent event) {
        EntityPlayer thePlayer = event.entityPlayer;
        Entity entity = event.target;

        ItemStack stack = thePlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof IToolCrowbar)
            thePlayer.swingItem();

        if (Game.isNotHost(thePlayer.worldObj))
            return;
        if (!ModuleManager.isModuleLoaded(Module.TRAIN))
            return;

        boolean used = false;
        if (stack != null && stack.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) stack.getItem();
            if (entity instanceof EntityMinecart) {
                EntityMinecart cart = (EntityMinecart) entity;

                if (crowbar.canLink(thePlayer, stack, cart)) {
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
                        crowbar.onLink(thePlayer, stack, cart);
                } else if (crowbar.canBoost(thePlayer, stack, cart)) {
                    thePlayer.addExhaustion(1F);

                    //noinspection StatementWithEmptyBody
                    if (thePlayer.ridingEntity != null) {
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
                    crowbar.onBoost(thePlayer, stack, cart);
                }
            }
        }
        if (used)
            event.setCanceled(true);
    }
}
