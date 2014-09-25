/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import mods.railcraft.common.core.StartupChecks;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import net.minecraft.client.Minecraft;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LatestVersionMessage {

    private boolean messageSent;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (!messageSent && StartupChecks.isVersionCheckComplete() && Minecraft.getMinecraft().thePlayer != null) {
            messageSent = true;
            if (StartupChecks.shouldSendMessage()) {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.update.message", StartupChecks.getLatestVersion());
//                "Railcraft " + StartupChecks.getLatestVersion() + " is available from <http://railcraft.info>");
            }
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }

}
