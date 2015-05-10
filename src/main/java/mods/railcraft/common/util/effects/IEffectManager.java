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
import java.io.IOException;
import java.util.Set;

import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import mods.railcraft.api.signals.IPairEffectRenderer;
import net.minecraft.tileentity.TileEntity;

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

    void fireSparkEffect(World world, double startX, double startY, double startZ, double endX, double endY, double endZ);

    void forceTrackSpawnEffect(World world, int x, int y, int z);
}
