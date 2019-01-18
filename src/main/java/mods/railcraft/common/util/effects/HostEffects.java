/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.effects;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.util.collections.ThrowingConsumer;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketEffect;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;

/**
 * Effects done on the logical server.
 */
public final class HostEffects implements Charge.IHostZapEffect {

    public static final HostEffects INSTANCE = new HostEffects();

    public static void init() {} // classloading

    private HostEffects() {
        Code.setValue(Charge.class, null, this, "hostEffects");
    }

    public void teleportEffect(Entity entity, Vec3d destination) {
        if (Game.isClient(entity.world))
            return;

        sendEffect(RemoteEffectType.TELEPORT, entity.world, entity.getPositionVector(), data -> {
            data.writeVec3d(entity.getPositionVector());
            data.writeVec3d(destination);
        });

        SoundHelper.playSoundAtEntity(entity, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.25F, 1.0F);
    }

    public void forceTrackSpawnEffect(World world, BlockPos pos, int color) {
        if (Game.isClient(world))
            return;

        sendEffect(RemoteEffectType.FORCE_SPAWN, world, pos, data -> {
            data.writeBlockPos(pos);
            data.writeInt(color);
        });

        SoundHelper.playSound(world, null, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.25F, 1.0F);
    }

    public void fireSparkEffect(World world, Vec3d start, Vec3d end) {
        if (Game.isClient(world))
            return;

        sendEffect(RemoteEffectType.FIRE_SPARK, world, start, data -> {
            data.writeVec3d(start);
            data.writeVec3d(end);
        });
    }

    @Override
    public void zapEffectDeath(World world, Object source) {
        if (Game.isClient(world))
            return;

        EffectManager.IEffectSource es = EffectManager.getEffectSource(source);
        sendEffect(RemoteEffectType.ZAP_DEATH, world, es.getPosF(), data -> data.writeVec3d(es.getPosF()));
    }

    public void blockCrack(World world, BlockPos source, Vec3d pos, Vec3d velocity, IBlockState state, String texture) {
        blockParticle(world, source, pos, velocity, state, texture, false);
    }

    public void blockDust(World world, BlockPos source, Vec3d pos, Vec3d velocity, IBlockState state, String texture) {
        blockParticle(world, source, pos, velocity, state, texture, true);
    }

    private void blockParticle(World world, BlockPos source, Vec3d pos, Vec3d velocity, IBlockState state, String texture, boolean dust) {
        sendEffect(RemoteEffectType.BLOCK_PARTICLE, world, pos, data -> {
            data.writeBlockPos(source);
            data.writeVec3d(pos);
            data.writeVec3d(velocity);
            data.writeInt(Block.getStateId(state));
            data.writeBoolean(dust);
            data.writeUTF(texture);
        });
    }

    private void sendEffect(RemoteEffectType type, World world, BlockPos pos, ThrowingConsumer<RailcraftOutputStream, IOException> writer) {
        preparePacket(type, writer).sendPacket(world, pos);
    }

    private void sendEffect(RemoteEffectType type, World world, Vec3d pos, ThrowingConsumer<RailcraftOutputStream, IOException> writer) {
        preparePacket(type, writer).sendPacket(world, pos);
    }

    private PacketEffect preparePacket(RemoteEffectType type, ThrowingConsumer<RailcraftOutputStream, IOException> writer) {
        PacketEffect pkt = new PacketEffect(type);
        try (RailcraftOutputStream data = pkt.getOutputStream()) {
            writer.accept(data);
        } catch (IOException ignored) {
        }

        return pkt;
    }
}
