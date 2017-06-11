/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.common.carts.*;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.modules.ModuleTrain;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.util.inventory.InvTools;
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
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof IToolCrowbar) {
            thePlayer.swingArm(event.getHand());
            event.setCanceled(true);
        } else
            return;

        if (Game.isClient(thePlayer.worldObj))
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
                    if (last != null && last.isEntityAlive()) {
                        LinkageManager lm = LinkageManager.instance();
                        if (lm.areLinked(cart, last, false)) {
                            lm.breakLink(cart, last);
                            used = true;
                            ChatPlugin.sendLocalizedHotBarMessageFromServer(thePlayer, "gui.railcraft.link.broken");
                            LinkageManager.printDebug("Reason For Broken Link: User removed link.");
                        } else {
                            used = lm.createLink(last, (EntityMinecart) entity);
                            if (used)
                                ChatPlugin.sendLocalizedHotBarMessageFromServer(thePlayer, "gui.railcraft.link.created");
                        }
                        if (!used)
                            ChatPlugin.sendLocalizedHotBarMessageFromServer(thePlayer, "gui.railcraft.link.failed");
                    } else {
                        linkMap.put(thePlayer, (EntityMinecart) entity);
                        ChatPlugin.sendLocalizedHotBarMessageFromServer(thePlayer, "gui.railcraft.link.started");
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
                        int lvl = RailcraftEnchantments.SMACK.getLevel(stack);
                        if (lvl == 0) {
                            CartTools.smackCart(cart, thePlayer, SMACK_VELOCITY);
                        }
                        float smackVelocity = SMACK_VELOCITY * (float) Math.pow(1.7, lvl);

                        Train train = Train.getTrain(cart);
                        smackVelocity /= (float) Math.pow(train.size(), 1D / (1 + lvl));
                        for (EntityMinecart each : train) {
                            CartTools.smackCart(cart, each, thePlayer, smackVelocity);
                        }

                    }
                crowbar.onBoost(thePlayer, hand, stack, cart);
            }
        }
    }
}
