/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.world.World;
import mods.railcraft.common.util.effects.EffectManager;

public class PacketEffect extends RailcraftPacket {

    public enum Effect {

        TELEPORT,
        FIRESPARK,
        FORCE_SPAWN
    };
    private Effect effect;
    private ByteArrayOutputStream bytes;
    private DataOutputStream outStream;

    public PacketEffect() {
        super();
    }

    public PacketEffect(Effect effect) {
        this.effect = effect;
    }

    public DataOutputStream getOutputStream() {
        if (outStream == null) {
            bytes = new ByteArrayOutputStream();
            outStream = new DataOutputStream(bytes);
        }
        return outStream;
    }

    public void sendPacket(World world, double x, double y, double z) {
        PacketDispatcher.sendToAllAround(this, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, 80));
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeByte(effect.ordinal());
        data.write(bytes.toByteArray());
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        EffectManager.instance.handleEffectPacket(data);
    }

    @Override
    public int getID() {
        return PacketType.EFFECT.ordinal();
    }

}
