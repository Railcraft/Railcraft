/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.lang.reflect.Method;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PacketDispatcher {

    private static final Class playerInstanceClass;
    private static final Method getOrCreateChunkWatcher;
    private static final Method sendToAllPlayersWatchingChunk;

    static {
        try {
            playerInstanceClass = PlayerManager.class.getDeclaredClasses()[0];
            getOrCreateChunkWatcher = ReflectionHelper.findMethod(PlayerManager.class, null, new String[]{"func_72690_a", "getOrCreateChunkWatcher"}, int.class, int.class, boolean.class);
            sendToAllPlayersWatchingChunk = ReflectionHelper.findMethod(playerInstanceClass, null, new String[]{"func_151251_a", "sendToAllPlayersWatchingChunk"}, Packet.class);
            getOrCreateChunkWatcher.setAccessible(true);
            sendToAllPlayersWatchingChunk.setAccessible(true);
        } catch (Exception ex) {
            Game.logThrowable("Reflection Failure in PacketDispatcher initalization {0} {1}", ex);
            throw new RuntimeException(ex);
        }
    }

    public static void sendToServer(RailcraftPacket packet) {
        PacketHandler.INSTANCE.channel.sendToServer(packet.getPacket());
    }

    public static void sendToPlayer(RailcraftPacket packet, EntityPlayerMP player) {
        PacketHandler.INSTANCE.channel.sendTo(packet.getPacket(), player);
    }

    public static void sendToAll(RailcraftPacket packet) {
        PacketHandler.INSTANCE.channel.sendToAll(packet.getPacket());
    }

    public static void sendToAllAround(RailcraftPacket packet, TargetPoint zone) {
        PacketHandler.INSTANCE.channel.sendToAllAround(packet.getPacket(), zone);
    }

    public static void sendToDimension(RailcraftPacket packet, int dimensionId) {
        PacketHandler.INSTANCE.channel.sendToDimension(packet.getPacket(), dimensionId);
    }

    public static void sendToWatchers(RailcraftPacket packet, WorldServer world, int worldX, int worldZ) {
        try {
            Object playerInstance = getOrCreateChunkWatcher.invoke(world.getPlayerManager(), worldX >> 4, worldZ >> 4, false);
            if (playerInstance != null)
                sendToAllPlayersWatchingChunk.invoke(playerInstance, (Packet) packet.getPacket());
        } catch (Exception ex) {
            Game.logThrowable("Reflection Failure in PacketDispatcher.sendToWatchers() {0} {1}", 20, ex, getOrCreateChunkWatcher.getName(), sendToAllPlayersWatchingChunk.getName());
            throw new RuntimeException(ex);
        }
    }

}
