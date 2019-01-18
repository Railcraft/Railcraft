/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.util.effects.RemoteEffectType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketEffect extends RailcraftPacket {

    private RemoteEffectType effect;
    private ByteArrayOutputStream bytes;
    private RailcraftOutputStream outStream;

    public PacketEffect() {

    }

    public PacketEffect(RemoteEffectType effect) {
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
        data.writeEnum(effect);
        data.write(bytes.toByteArray());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        data.readEnum(RemoteEffectType.VALUES).handle(ClientEffects.INSTANCE, data);
    }

    @Override
    public int getID() {
        return PacketType.EFFECT.ordinal();
    }

}
