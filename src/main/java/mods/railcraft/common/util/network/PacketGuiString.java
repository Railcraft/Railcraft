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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.IOException;

public class PacketGuiString extends RailcraftPacket {

    private byte windowId, dataId;
    private String str;

    public PacketGuiString() {
    }

    public PacketGuiString(int windowId, int dataId, String str) {
        this.windowId = (byte) windowId;
        this.dataId = (byte) dataId;
        this.str = str;
    }

    public void sendPacket(EntityPlayerMP player) {
        PacketDispatcher.sendToPlayer(this, player);
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(windowId);
        data.writeByte(dataId);
        data.writeUTF(str);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        windowId = data.readByte();
        dataId = data.readByte();
        str = data.readUTF();

        EntityPlayerSP player = FMLClientHandler.instance().getClient().player;

        if (player.openContainer instanceof RailcraftContainer && player.openContainer.windowId == windowId)
            ((RailcraftContainer) player.openContainer).updateString(dataId, str);
    }

    @Override
    public int getID() {
        return PacketType.GUI_STRING.ordinal();
    }

}
