/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class PacketDispatcher {

    public static void sendToServer(RailcraftPacket packet) {
        PacketHandler.INSTANCE.channel.sendToServer(packet.getPacket());
    }

    public static void sendToPlayer(RailcraftPacket packet, EntityPlayerMP player) {
        PacketHandler.INSTANCE.channel.sendTo(packet.getPacket(), player);
    }

    public static void sendToPlayer(Packet<?> packet, EntityPlayerMP player) {
        player.connection.sendPacket(packet);
    }

    public static void sendToAll(RailcraftPacket packet) {
        PacketHandler.INSTANCE.channel.sendToAll(packet.getPacket());
    }

    public static TargetPoint targetPoint(WorldCoordinate point, double range) {
        return targetPoint(point.getDim(), point.getPos(), range);
    }

    public static TargetPoint targetPoint(int dim, BlockPos pos, double range) {
        return new TargetPoint(dim, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, range);
    }

    public static TargetPoint targetPoint(int dim, Vec3d vec, double range) {
        return new TargetPoint(dim, vec.x, vec.y, vec.z, range);
    }

    public static TargetPoint targetPoint(int dim, double x, double y, double z, double range) {
        return new TargetPoint(dim, x, y, z, range);
    }

    public static void sendToAllAround(RailcraftPacket packet, TargetPoint zone) {
        PacketHandler.INSTANCE.channel.sendToAllAround(packet.getPacket(), zone);
    }

    public static void sendToDimension(RailcraftPacket packet, int dimensionId) {
        PacketHandler.INSTANCE.channel.sendToDimension(packet.getPacket(), dimensionId);
    }

    public static void sendToWatchers(RailcraftPacket packet, WorldServer world, int worldX, int worldZ) {
        sendToWatchers(packet.getPacket(), world, worldX, worldZ);
    }

    public static void sendToWatchers(Packet<?> packet, WorldServer world, int worldX, int worldZ) {
        int chunkX = worldX >> 4;
        int chunkZ = worldZ >> 4;

        PlayerChunkMapEntry chunkManager = world.getPlayerChunkMap().getEntry(chunkX, chunkZ);
        if (chunkManager != null)
            chunkManager.sendPacket(packet);
    }

}
