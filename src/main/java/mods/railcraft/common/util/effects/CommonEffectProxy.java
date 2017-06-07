/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.effects;

import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketEffect;
import mods.railcraft.common.util.network.PacketEffect.Effect;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CommonEffectProxy implements IEffectManager {
    protected static final Random rand = new Random();

    @Override
    public void teleportEffect(Entity entity, Vec3d destination) {
        if (Game.isClient(entity.worldObj))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.TELEPORT);
            RailcraftOutputStream data = pkt.getOutputStream();
            data.writeVec3d(entity.getPositionVector());
            data.writeVec3d(destination);
            pkt.sendPacket(entity.worldObj, entity.getPositionVector());
        } catch (IOException ignored) {
        }

        SoundHelper.playSoundAtEntity(entity, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.25F, 1.0F);
    }

    @Override
    public void forceTrackSpawnEffect(World world, BlockPos pos) {
        if (Game.isClient(world))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.FORCE_SPAWN);
            RailcraftOutputStream data = pkt.getOutputStream();
            data.writeBlockPos(pos);
            pkt.sendPacket(world, pos);
        } catch (IOException ignored) {
        }

        SoundHelper.playSound(world, null, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.25F, 1.0F);
    }

    @Override
    public void fireSparkEffect(World world, Vec3d start, Vec3d end) {
        if (Game.isClient(world))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.FIRESPARK);
            RailcraftOutputStream data = pkt.getOutputStream();
            data.writeVec3d(start);
            data.writeVec3d(end);
            pkt.sendPacket(world, start);
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean isTuningAuraActive() {
        return false;
    }

    @Override
    public boolean isGoggleAuraActive(ItemGoggles.GoggleAura aura) {
        return false;
    }

    @Override
    public void tuningEffect(TileEntity start, TileEntity dest) {
    }

    @Override
    public void trailEffect(BlockPos start, TileEntity dest, long colorSeed) {
    }

    @Override
    public void chunkLoaderEffect(World world, Object source, Set<ChunkPos> chunks) {
    }

    @Override
    public void handleEffectPacket(RailcraftInputStream data) throws IOException {
    }

    protected void spawnParticle(Particle particle) {
    }

    @Override
    public void steamEffect(World world, Object source, double yOffset) {
    }

    @Override
    public void steamJetEffect(World world, Object source, Vec3d vel) {
    }

    @Override
    public void chimneyEffect(World world, double x, double y, double z, EnumColor color) {
    }

    @Override
    public void locomotiveEffect(World world, double x, double y, double z) {
    }

    @Override
    public void zapEffectPoint(World world, Object source) {
    }

    @Override
    public void zapEffectDeath(World world, Object source) {
        if (Game.isClient(world))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.ZAP_DEATH);
            RailcraftOutputStream data = pkt.getOutputStream();
            EffectManager.IEffectSource es = EffectManager.getEffectSource(source);
            data.writeVec3d(es.getPosF());
            pkt.sendPacket(world, es.getPosF());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void zapEffectSurface(IBlockState stateIn, World worldIn, BlockPos pos) {
    }
}
