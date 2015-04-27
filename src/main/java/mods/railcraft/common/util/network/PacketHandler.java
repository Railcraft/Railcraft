/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftPacket.PacketType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketHandler {
    public static final PacketHandler INSTANCE = new PacketHandler();
    private static final PacketType[] packetTypes = PacketType.values();
    final FMLEventChannel channel;

    private PacketHandler() {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(RailcraftPacket.CHANNEL_NAME);
        channel.register(this);
    }

    public static void init() {
        // NOOP
    }

    @SubscribeEvent
    public void onPacket(ServerCustomPacketEvent event) {
        byte[] data = new byte[event.packet.payload().readableBytes()];
        event.packet.payload().readBytes(data);

        onPacketData(data, ((NetHandlerPlayServer) event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onPacket(ClientCustomPacketEvent event) {
        byte[] data = new byte[event.packet.payload().readableBytes()];
        event.packet.payload().readBytes(data);

//        System.out.println("Packet Received!");

        onPacketData(data, null);
    }

    public void onPacketData(byte[] bData, EntityPlayerMP player) {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bData));
        try {
            RailcraftPacket pkt;

            byte packetID = data.readByte();

            if (packetID < 0)
                return;

//            System.out.println("Packet Received: " + packetID);
            PacketType type = packetTypes[packetID];
            switch (type) {
                case TILE_ENTITY:
                    pkt = new PacketTileEntity();
                    break;
                case GUI_RETURN:
                    pkt = new PacketGuiReturn(player);
                    break;
                case TILE_EXTRA_DATA:
                    pkt = new PacketTileExtraData();
                    break;
                case TILE_REQUEST:
                    pkt = new PacketTileRequest(player);
                    break;
                case GUI_INTEGER:
                    pkt = new PacketGuiInteger();
                    break;
                case GUI_STRING:
                    pkt = new PacketGuiString();
                    break;
                case GUI_WIDGET:
                    pkt = new PacketGuiWidget();
                    break;
                case EFFECT:
                    pkt = new PacketEffect();
                    break;
                case CONTROLLER_UPDATE:
                case RECEIVER_UPDATE:
                case SIGNAL_UPDATE:
                    pkt = new PacketPairUpdate(type);
                    break;
                case CONTROLLER_REQUEST:
                case RECEIVER_REQUEST:
                case SIGNAL_REQUEST:
                    pkt = new PacketPairRequest(player, type);
                    break;
                case ITEM_NBT:
                    pkt = new PacketCurrentItemNBT((EntityPlayer) player, ((EntityPlayer) player).getCurrentEquippedItem());
                    break;
                case KEY_PRESS:
                    pkt = new PacketKeyPress(player);
                    break;
                case GOLDEN_TICKET_GUI:
                    pkt = new PacketTicketGui();
                    break;
                default:
                    return;
            }
            if (pkt != null)
                pkt.readData(data);
        } catch (IOException e) {
            Game.logThrowable("Exception in PacketHandler.onPacketData: {0}", e, Arrays.toString(bData));
        }
    }
}
