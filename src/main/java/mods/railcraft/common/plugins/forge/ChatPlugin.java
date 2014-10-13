/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChatPlugin {

    public static IChatComponent getMessage(String msg) {
        return new ChatComponentText(msg);
    }

    public static void sendLocalizedChat(EntityPlayer player, String msg, Object... args) {
        player.addChatMessage(getMessage(String.format(LocalizationPlugin.translate(msg), args)));
    }

    public static void sendLocalizedChatFromClient(EntityPlayer player, String msg, Object... args) {
        if (Game.isNotHost(player.worldObj))
            player.addChatMessage(getMessage(String.format(LocalizationPlugin.translate(msg), args)));
    }

}
