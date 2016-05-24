/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.effects;

import mods.railcraft.api.signals.IPairEffectRenderer;
import mods.railcraft.common.items.ItemGoggles;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEffectManager extends IPairEffectRenderer {
    void chunkLoaderEffect(World world, Object source, Set<ChunkCoordIntPair> chunks);

    boolean isGoggleAuraActive(ItemGoggles.GoggleAura aura);

    void handleEffectPacket(DataInputStream data) throws IOException;

    void steamEffect(World world, Object source, double yOffset);

    void steamJetEffect(World world, Object source, double vecX, double vecY, double vecZ);

    void chimneyEffect(World world, double x, double y, double z);

    void teleportEffect(Entity entity, double destX, double destY, double destZ);

    void trailEffect(int startX, int startY, int startZ, TileEntity dest, long colorSeed);

    void fireSparkEffect(World world, Vec3d start, Vec3d end);

    void forceTrackSpawnEffect(World world, int x, int y, int z);
}
