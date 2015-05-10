/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.effects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import mods.railcraft.common.items.ItemGoggles;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.entity.Entity;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketEffect;
import mods.railcraft.common.util.network.PacketEffect.Effect;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.util.MathHelper;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CommonEffectProxy implements IEffectManager {
    protected static final Random rand = new Random();

    @Override
    public void teleportEffect(Entity entity, double destX, double destY, double destZ) {
        if (Game.isNotHost(entity.worldObj))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.TELEPORT);
            DataOutputStream data = pkt.getOutputStream();
            data.writeDouble(entity.posX);
            data.writeDouble(entity.posY);
            data.writeDouble(entity.posZ);
            data.writeDouble(destX);
            data.writeDouble(destY);
            data.writeDouble(destZ);
            pkt.sendPacket(entity.worldObj, entity.posX, entity.posY, entity.posZ);
        } catch (IOException ex) {
        }

        SoundHelper.playSoundAtEntity(entity, "mob.endermen.portal", 0.25F, 1.0F);
    }

    @Override
    public void forceTrackSpawnEffect(World world, int x, int y, int z) {
        if (Game.isNotHost(world))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.FORCE_SPAWN);
            DataOutputStream data = pkt.getOutputStream();
            data.writeInt(x);
            data.writeInt(y);
            data.writeInt(z);
            pkt.sendPacket(world, x, y, z);
        } catch (IOException ex) {
        }

        SoundHelper.playSound(world, x, y, z, "mob.endermen.portal", 0.25F, 1.0F);
    }

    @Override
    public void fireSparkEffect(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        if (Game.isNotHost(world))
            return;

        try {
            PacketEffect pkt = new PacketEffect(Effect.FIRESPARK);
            DataOutputStream data = pkt.getOutputStream();
            data.writeDouble(startX);
            data.writeDouble(startY);
            data.writeDouble(startZ);
            data.writeDouble(endX);
            data.writeDouble(endY);
            data.writeDouble(endZ);
            pkt.sendPacket(world, startX, startY, startZ);
        } catch (IOException ex) {
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
    public void trailEffect(int startX, int startY, int startZ, TileEntity dest, long colorSeed) {
    }

    @Override
    public void chunkLoaderEffect(World world, Object source, Set<ChunkCoordIntPair> chunks) {
    }

    @Override
    public void handleEffectPacket(DataInputStream data) throws IOException {
    }

    protected void spawnParticle(EntityFX particle) {
    }

    @Override
    public void steamEffect(World world, Object source, double yOffset) {
    }

    @Override
    public void steamJetEffect(World world, Object source, double vecX, double vecY, double vecZ) {
    }

    @Override
    public void chimneyEffect(World world, double x, double y, double z) {
    }
}
