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
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;

public abstract class RailcraftPacket {

    public static final String CHANNEL_NAME = "RC";

    // todo add Function<EntityPlayer, RailcraftPacket> to param
    public enum PacketType {

        TILE_ENTITY,
        GUI_RETURN,
        TILE_EXTRA_DATA,
        TILE_REQUEST,
        GUI_INTEGER,
        GUI_STRING,
        GUI_DATA,
        GUI_WIDGET,
        EFFECT,
        CONTROLLER_REQUEST, CONTROLLER_UPDATE,
        RECEIVER_REQUEST, RECEIVER_UPDATE,
        SIGNAL_REQUEST, SIGNAL_UPDATE,
        ITEM_NBT_HAND,
        ITEM_NBT_TILE,
        KEY_PRESS,
        GOLDEN_TICKET_GUI,
        LOGBOOK_GUI,
        SHUNTING_AURA,
        MOVING_SOUND,
        STOP_RECORD,
        ENTITY_SYNC
    }

    public FMLProxyPacket getPacket() {
        ByteBuf byteBuf = Unpooled.buffer();
        try (ByteBufOutputStream out = new ByteBufOutputStream(byteBuf);
             RailcraftOutputStream data = new RailcraftOutputStream(out)) {
            data.writeByte(getID());
            writeData(data);
            return new FMLProxyPacket(new PacketBuffer(byteBuf), CHANNEL_NAME);
        } catch (IOException e) {
            Game.log().throwable("Error constructing packet: {0}", e, getClass());
            if (Game.DEVELOPMENT_VERSION)
                throw new RuntimeException(e);
        }
        PacketBuffer buffer = new PacketBuffer(byteBuf);
        buffer.writeByte(-1);
        return new FMLProxyPacket(buffer, CHANNEL_NAME);
    }

    public abstract void writeData(RailcraftOutputStream data) throws IOException;

    public abstract void readData(RailcraftInputStream data) throws IOException;

    public abstract int getID(); // TODO use type

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
