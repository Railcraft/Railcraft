/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.ISignalPacketBuilder;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.network.PacketKeyPress.EnumKeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PacketBuilder implements ISignalPacketBuilder {

    private static PacketBuilder instance;

    @Nonnull
    public static PacketBuilder instance() {
        if (instance == null)
            instance = new PacketBuilder();
        return instance;
    }

    private PacketBuilder() {
    }

    public void sendTileEntityPacket(RailcraftTileEntity tile) {
        if (tile.getWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) tile.getWorld();
            PacketTileEntity pkt = new PacketTileEntity(tile);
            PacketDispatcher.sendToWatchers(pkt, world, tile.getPos().getX(), tile.getPos().getZ());
        }
    }

    @Override
    public void sendPairPacketUpdate(AbstractPair pairing) {
        PacketPairUpdate pkt = new PacketPairUpdate(pairing);
        PacketDispatcher.sendToDimension(pkt, pairing.getTile().getWorld().provider.getDimension());
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

    public void sendGuiIntegerPacket(IContainerListener listener, int windowId, int key, int value) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiInteger pkt = new PacketGuiInteger(windowId, key, value);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGuiStringPacket(IContainerListener listener, int windowId, int key, String value) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiString pkt = new PacketGuiString(windowId, key, value);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGuiWidgetPacket(IContainerListener listener, int windowId, int widgetId, byte[] data) {
        if (listener instanceof EntityPlayerMP) {
            PacketGuiWidget pkt = new PacketGuiWidget(windowId, widgetId, data);
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

    public void sendGoldenTicketGuiPacket(IContainerListener listener) {
        if (listener instanceof EntityPlayerMP) {
            PacketTicketGui pkt = new PacketTicketGui();
            PacketDispatcher.sendToPlayer(pkt, (EntityPlayerMP) listener);
        }
    }

}
