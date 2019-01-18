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

/**
 * This packet may be unused
 */
public class PacketGuiData extends RailcraftPacket {

    private byte windowId, dataId;
    private byte[] payload;

    public PacketGuiData() {

    }

    public PacketGuiData(int windowId, int dataId,  byte[] payload) {
        this.windowId = (byte) windowId;
        this.dataId = (byte) dataId;
        this.payload = payload;
    }

    public void sendPacket(EntityPlayerMP player) {
        PacketDispatcher.sendToPlayer(this, player);
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(windowId);
        data.writeByte(dataId);
        data.write(payload);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        windowId = data.readByte();
        dataId = data.readByte();

        EntityPlayerSP player = FMLClientHandler.instance().getClient().player;

        if (player.openContainer instanceof RailcraftContainer && player.openContainer.windowId == windowId)
            ((RailcraftContainer) player.openContainer).updateData(dataId, data);
    }

    @Override
    public int getID() {
        return PacketType.GUI_DATA.ordinal();
    }

}
