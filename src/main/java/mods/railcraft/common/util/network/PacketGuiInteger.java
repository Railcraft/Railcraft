/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.IOException;

public class PacketGuiInteger extends RailcraftPacket {

    private int windowId, dataId, value;

    public PacketGuiInteger() {
    }

    public PacketGuiInteger(int windowId, int dataId, int value) {
        this.windowId = windowId;
        this.dataId = dataId;
        this.value = value;
    }

    public void sendPacket(EntityPlayerMP player) {
        PacketDispatcher.sendToPlayer(this, player);
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(windowId);
        data.writeByte(dataId);
        data.writeInt(value);
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        windowId = data.readByte();
        dataId = data.readByte();
        value = data.readInt();

        EntityPlayerSP player = FMLClientHandler.instance().getClient().player;

        if (player.openContainer != null && player.openContainer.windowId == windowId)
            player.openContainer.updateProgressBar(dataId, value);
    }

    @Override
    public int getID() {
        return PacketType.GUI_INTEGER.ordinal();
    }

}
