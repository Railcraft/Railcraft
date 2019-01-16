/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.IOException;

public class PacketGuiWidget extends RailcraftPacket {

    private byte windowId;
    private Widget widget;
    private byte[] payload;

    public PacketGuiWidget() {
    }

    public PacketGuiWidget(int windowId, Widget widget, byte[] data) {
        this.windowId = (byte) windowId;
        this.widget = widget;
        this.payload = data;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(windowId);
        data.writeByte(widget.getId());
        data.write(payload);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        windowId = data.readByte();
        byte widgetId = data.readByte();

        EntityPlayerSP player = FMLClientHandler.instance().getClient().player;

        if (player.openContainer instanceof RailcraftContainer && player.openContainer.windowId == windowId) {
            RailcraftContainer railcraftContainer = ((RailcraftContainer) player.openContainer);
            railcraftContainer.getWidgets().get(widgetId).readServerSyncData(data);
        }
    }

    @Override
    public int getID() {
        return PacketType.GUI_WIDGET.ordinal();
    }

}
