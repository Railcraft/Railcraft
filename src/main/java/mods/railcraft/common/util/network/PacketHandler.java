/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftPacket.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PacketHandler {
    public static final PacketHandler INSTANCE = new PacketHandler();
    private static final PacketType[] packetTypes = PacketType.values();
    final FMLEventChannel channel;

    private PacketHandler() {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(RailcraftPacket.CHANNEL_NAME);
        channel.register(this);
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
        // NOOP
    }

    @SubscribeEvent
    public void onPacket(ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;
        onPacketData(event.getPacket().payload(), player, player.getServer());
    }

    @SubscribeEvent
    public void onPacket(ClientCustomPacketEvent event) {
        onPacketData(event.getPacket().payload(), null, Minecraft.getMinecraft());
    }

    private void onPacketData(ByteBuf byteBuf, EntityPlayerMP player, @Nullable IThreadListener listener) {
        RailcraftInputStream data = new RailcraftInputStream(new ByteBufInputStream(byteBuf));
        try {
            RailcraftPacket pkt;

            byte packetID = data.readByte();

            if (packetID < 0)
                return;

//            System.out.println("Packet Received: " + packetID);
            PacketType type = packetTypes[packetID];
            switch (type) {
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
                case ITEM_NBT_HAND:
                    pkt = new PacketItemNBT.CurrentItem(player);
                    break;
                case ITEM_NBT_TILE:
                    pkt = new PacketItemNBT.RoutableTile(player);
                    break;
                case KEY_PRESS:
                    pkt = new PacketKeyPress(player);
                    break;
                case GOLDEN_TICKET_GUI:
                    pkt = new PacketTicketGui();
                    break;
                case LOGBOOK_GUI:
                    pkt = new PacketLogbook();
                    break;
                case SHUNTING_AURA:
                    pkt = new PacketShuntingAura();
                    break;
                case MOVING_SOUND:
                    pkt = new PacketMovingSound();
                    break;
                case STOP_RECORD:
                    pkt = new PacketStopRecord();
                    break;
                case ENTITY_SYNC:
                    pkt = new PacketEntitySync();
                    break;
                default:
                    return;
            }
            readPacket(pkt, data, listener);
        } catch (IOException e) {
            Game.log().throwable("Exception in PacketHandler.onPacketData", e);
        }
    }

    private static void readPacket(final RailcraftPacket packet, final RailcraftInputStream data, @Nullable IThreadListener threadListener) {
        if (threadListener != null && !threadListener.isCallingFromMinecraftThread()) {
            threadListener.addScheduledTask(() -> {
                try {
                    packet.readData(data);
                } catch (IOException e) {
                    Game.log().throwable("Exception in PacketHandler.readPacket", 10, e);
                }
            });
        }
    }
}
