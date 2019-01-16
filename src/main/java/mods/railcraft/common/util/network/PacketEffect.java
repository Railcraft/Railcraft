/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketEffect extends RailcraftPacket {

    public enum Effect {

        TELEPORT,
        FIRESPARK,
        FORCE_SPAWN,
        ZAP_DEATH;
        public static final Effect[] VALUES = values();
    }

    private Effect effect;
    private ByteArrayOutputStream bytes;
    private RailcraftOutputStream outStream;

    public PacketEffect() {

    }

    public PacketEffect(Effect effect) {
        this.effect = effect;
    }

    public RailcraftOutputStream getOutputStream() {
        if (outStream == null) {
            bytes = new ByteArrayOutputStream();
            outStream = new RailcraftOutputStream(bytes);
        }
        return outStream;
    }

    public void sendPacket(World world, BlockPos pos) {
        PacketDispatcher.sendToAllAround(this, PacketDispatcher.targetPoint(world.provider.getDimension(), pos, 80));
    }

    public void sendPacket(World world, Vec3d vec) {
        PacketDispatcher.sendToAllAround(this, PacketDispatcher.targetPoint(world.provider.getDimension(), vec, 80));
    }

    public void sendPacket(World world, double x, double y, double z) {
        PacketDispatcher.sendToAllAround(this, PacketDispatcher.targetPoint(world.provider.getDimension(), x, y, z, 80));
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeByte(effect.ordinal());
        data.write(bytes.toByteArray());
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        EffectManager.instance.handleEffectPacket(data);
    }

    @Override
    public int getID() {
        return PacketType.EFFECT.ordinal();
    }

}
