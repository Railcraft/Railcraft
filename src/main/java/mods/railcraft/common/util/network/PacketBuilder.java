/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import net.minecraft.world.WorldServer;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.ISignalPacketBuilder;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.network.PacketKeyPress.EnumKeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PacketBuilder implements ISignalPacketBuilder {

    private static PacketBuilder instance;

    public static PacketBuilder instance() {
        if (instance == null)
            instance = new PacketBuilder();
        return instance;
    }

    private PacketBuilder() {
    }

    public void sendTileEntityPacket(RailcraftTileEntity tile) {
        if (tile.getWorldObj() instanceof WorldServer) {
            WorldServer world = (WorldServer) tile.getWorldObj();
            PacketTileEntity pkt = new PacketTileEntity(tile);
            PacketDispatcher.sendToWatchers(pkt, world, tile.xCoord, tile.zCoord);
        }
    }

    @Override
    public void sendPairPacketUpdate(AbstractPair pairing) {
        PacketPairUpdate pkt = new PacketPairUpdate(pairing);
        PacketDispatcher.sendToDimension(pkt, pairing.getTile().getWorldObj().provider.dimensionId);
    }

    @Override
    public void sendPairPacketRequest(AbstractPair pairing) {
        PacketPairRequest pkt = new PacketPairRequest(pairing);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiReturnPacket(IGuiReturnHandler handler) {
        PacketGuiReturn pkt = new PacketGuiReturn(handler);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiReturnPacket(IGuiReturnHandler handler, byte[] extraData) {
        PacketGuiReturn pkt = new PacketGuiReturn(handler, extraData);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendKeyPressPacket(EnumKeyBinding keyPress) {
        PacketKeyPress pkt = new PacketKeyPress(keyPress);
        PacketDispatcher.sendToServer(pkt);
    }

    public void sendGuiIntegerPacket(EntityPlayerMP player, int windowId, int key, int value) {
        PacketGuiInteger pkt = new PacketGuiInteger(windowId, key, value);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    public void sendGuiStringPacket(EntityPlayerMP player, int windowId, int key, String value) {
        PacketGuiString pkt = new PacketGuiString(windowId, key, value);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    public void sendGuiWidgetPacket(EntityPlayerMP player, int windowId, int widgetId, byte[] data) {
        PacketGuiWidget pkt = new PacketGuiWidget(windowId, widgetId, data);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    public void sendGoldenTicketGuiPacket(EntityPlayerMP player) {
        PacketTicketGui pkt = new PacketTicketGui();
        PacketDispatcher.sendToPlayer(pkt, player);
    }

}
