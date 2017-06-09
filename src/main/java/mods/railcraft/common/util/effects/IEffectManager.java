/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.effects;

import mods.railcraft.api.signals.IPairEffectRenderer;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.network.RailcraftInputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEffectManager extends IPairEffectRenderer {
    void chunkLoaderEffect(World world, Object source, Set<ChunkPos> chunks);

    boolean isGoggleAuraActive(ItemGoggles.GoggleAura aura);

    void handleEffectPacket(RailcraftInputStream data) throws IOException;

    void steamEffect(World world, Object source, double yOffset);

    void steamJetEffect(World world, Object source, Vec3d vel);

    void chimneyEffect(World world, double x, double y, double z, EnumColor color);

    void locomotiveEffect(World world, double x, double y, double z);

    void zapEffectPoint(World world, Object source);

    void zapEffectDeath(World world, Object source);

    void zapEffectSurface(IBlockState stateIn, World worldIn, BlockPos pos);

    void teleportEffect(Entity entity, Vec3d destination);

    void trailEffect(BlockPos start, TileEntity dest, long colorSeed);

    void fireSparkEffect(World world, Vec3d start, Vec3d end);

    void forceTrackSpawnEffect(World world, BlockPos pos);
}
