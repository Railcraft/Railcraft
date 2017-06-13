/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import com.mojang.authlib.GameProfile;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

/**
 * Chat Plugin for sending chat messages.
 *
 * Don't use the LocalizationPlugin in conjunction with this class,
 * it will result in everything being translated to English only.
 *
 * This is because the server only knows about English, only the client can do proper translations.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChatPlugin {
    public static ITextComponent makeMessage(String msg) {
        return new TextComponentString(msg);
    }

    public static ITextComponent translateMessage(String msg, Object... args) {
        return new TextComponentTranslation(msg, args);
    }

    /**
     * Don't use this from the server thread! It will not translate stuff correctly!
     */
    public static void sendLocalizedChat(EntityPlayer player, String msg, Object... args) {
        player.addChatMessage(makeMessage(String.format(LocalizationPlugin.translate(msg), args)));
    }

    public static void sendLocalizedChatFromClient(@Nullable EntityPlayer player, String msg, Object... args) {
        if (player != null && Game.isClient(player.worldObj))
            sendLocalizedChat(player, msg, args);
    }

    public static void sendLocalizedChatFromServer(@Nullable EntityPlayer player, String msg, Object... args) {
        if (player != null && Game.isHost(player.worldObj)) {
            modifyArgs(args);
            player.addChatMessage(translateMessage(msg, args));
        }
    }

    public static void sendLocalizedHotBarMessageFromServer(@Nullable EntityPlayer player, String msg, Object... args) {
        if (player instanceof EntityPlayerMP && Game.isHost(player.worldObj)) {
            modifyArgs(args);
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketChat(translateMessage(msg, args), (byte) 2));
        }
    }
 
    static void modifyArgs(Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = translateMessage((String) args[i]);
            } else if (args[i] instanceof GameProfile) {
                String username = ((GameProfile) args[i]).getName();
                args[i] = username != null ? username : "[unknown]";
            }
        }
    }

    public static void sendLocalizedChatToAllFromServer(World world, String msg, Object... args) {
        if (world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) world;
            for (Object obj : worldServer.playerEntities) {
                if (obj instanceof EntityPlayer)
                    sendLocalizedChat((EntityPlayer) obj, msg, args);
            }
        }
    }
}
