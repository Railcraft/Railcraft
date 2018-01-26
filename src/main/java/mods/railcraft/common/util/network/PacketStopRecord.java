package mods.railcraft.common.util.network;

import mods.railcraft.common.carts.EntityCartJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

class PacketStopRecord extends RailcraftPacket {

    private int id;

    PacketStopRecord() {
    }

    PacketStopRecord(EntityCartJukebox cart) {
        this.id = cart.getEntityId();
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeInt(id);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        int id = data.readInt();
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(id);
        if (!(entity instanceof EntityCartJukebox))
            return;
        ((EntityCartJukebox) entity).music = null;
    }

    @Override
    public int getID() {
        return PacketType.STOP_RECORD.ordinal();
    }
}
