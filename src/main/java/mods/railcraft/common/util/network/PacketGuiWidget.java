/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.client.FMLClientHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import net.minecraft.client.entity.EntityClientPlayerMP;

public class PacketGuiWidget extends RailcraftPacket {

    private byte windowId, widgetId;
    private byte[] payload;

    public PacketGuiWidget() {
        super();
    }

    public PacketGuiWidget(int windowId, int widgetId, byte[] data) {
        this.windowId = (byte) windowId;
        this.widgetId = (byte) widgetId;
        this.payload = data;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeByte(windowId);
        data.writeByte(widgetId);
        data.write(payload);
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        windowId = data.readByte();
        widgetId = data.readByte();

        EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;

        if (player.openContainer instanceof RailcraftContainer && player.openContainer.windowId == windowId)
            ((RailcraftContainer) player.openContainer).handleWidgetClientData(widgetId, data);
    }

    @Override
    public int getID() {
        return PacketType.GUI_WIDGET.ordinal();
    }

}
