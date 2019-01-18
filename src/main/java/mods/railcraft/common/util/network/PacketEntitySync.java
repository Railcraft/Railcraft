/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.network;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.common.util.misc.Code;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

class PacketEntitySync extends RailcraftPacket {
    private @Nullable Entity entity;
    private INetworkedObject<DataInputStream, DataOutputStream> networkedObject;

    PacketEntitySync() {
    }

    PacketEntitySync(Entity entity) {
        this.entity = entity;
        Code.assertInstance(INetworkedObject.class, entity);
        this.networkedObject = Code.cast(entity);
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        Objects.requireNonNull(entity);
        data.writeInt(entity.getEntityId());
        networkedObject.writePacketData(data);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        entity = Minecraft.getMinecraft().world.getEntityByID(data.readInt());
        if (entity instanceof INetworkedObject) {
            networkedObject = Code.cast(entity);
            networkedObject.readPacketData(data);
        }
    }

    @Override
    public int getID() {
        return PacketType.ENTITY_SYNC.ordinal();
    }
}
