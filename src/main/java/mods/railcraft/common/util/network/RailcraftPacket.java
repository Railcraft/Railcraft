/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.util.misc.Game;

public abstract class RailcraftPacket {

    public final static String CHANNEL_NAME = "RC";

    public enum PacketType {

        TILE_ENTITY,
        GUI_RETURN,
        TILE_EXTRA_DATA,
        TILE_REQUEST,
        GUI_INTEGER,
        GUI_STRING,
        GUI_WIDGET,
        EFFECT,
        CONTROLLER_REQUEST, CONTROLLER_UPDATE,
        RECEIVER_REQUEST, RECEIVER_UPDATE,
        SIGNAL_REQUEST, SIGNAL_UPDATE,
        ITEM_NBT,
        KEY_PRESS,
        GOLDEN_TICKET_GUI,
    }

    public FMLProxyPacket getPacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeByte(getID());
            writeData(data);
        } catch (IOException e) {
            Game.logThrowable("Error constructing packet: {0}", e, getClass());
        }
        return new FMLProxyPacket(Unpooled.wrappedBuffer(bytes.toByteArray()), CHANNEL_NAME);
    }

    public abstract void writeData(DataOutputStream data) throws IOException;

    public abstract void readData(DataInputStream data) throws IOException;

    public abstract int getID();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
