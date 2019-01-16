/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.particles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: fix or delete this
public class ParticleHelper {
    public static final float SMOKE_GRAVITY = -0.1F;

    public static final Random RANDOM = new Random();

    @SideOnly(Side.CLIENT)
    public static boolean addHitEffects(World world, Block block, RayTraceResult target, ParticleManager manager, @Nullable IParticleHelperCallback callback) {
//        BlockPos pos = target.getBlockPos();
//        int x = pos.getX();
//        int y = pos.getY();
//        int z = pos.getZ();
//
//        EnumFacing sideHit = target.sideHit;
//
//        if (block != WorldPlugin.getBlock(world, x, y, z)) return true;
//        int meta = world.getBlockMetadata(x, y, z);
//
//        float b = 0.1F;
//        double px = x + RANDOM.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F)) + b + block.getBlockBoundsMinX();
//        double py = y + RANDOM.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F)) + b + block.getBlockBoundsMinY();
//        double pz = z + RANDOM.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F)) + b + block.getBlockBoundsMinZ();
//
//        if (sideHit == DOWN)
//            py = (double) y + block.getBlockBoundsMinY() - (double) b;
//
//        if (sideHit == UP)
//            py = (double) y + block.getBlockBoundsMaxY() + (double) b;
//
//        if (sideHit == NORTH)
//            pz = (double) z + block.getBlockBoundsMinZ() - (double) b;
//
//        if (sideHit == SOUTH)
//            pz = (double) z + block.getBlockBoundsMaxZ() + (double) b;
//
//        if (sideHit == WEST)
//            px = (double) x + block.getBlockBoundsMinX() - (double) b;
//
//        if (sideHit == EAST)
//            px = (double) x + block.getBlockBoundsMaxX() + (double) b;
//        EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, 0.0D, 0.0D, 0.0D, block, sideHit, meta);
//        fx.setParticleIcon(block.getIcon(world, x, y, z, 0));
//        if (callback != null)
//            callback.addHitEffects(fx, world, x, y, z, meta);
//        manager.addEffect(fx.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

        return true;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature of how
     * this is invoked, the x/y/z locations are not always guaranteed to host
     * your block. So be sure to do proper sanity checks before assuming that
     * the location is this block.
     *
     * @param world   The current world
     * @param manager A reference to the current effect renderer.
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public static boolean addDestroyEffects(World world, Block block, BlockPos pos, IBlockState state, ParticleManager manager, @Nullable IParticleHelperCallback callback) {
//        if (!WorldPlugin.isBlockAt(world, pos, block)) return true;
//        byte its = 4;
//        int x = pos.getX();
//        int y = pos.getY();
//        int z = pos.getZ();
//        for (int i = 0; i < its; ++i) {
//            for (int j = 0; j < its; ++j) {
//                for (int k = 0; k < its; ++k) {
//                    double px = x + (i + 0.5D) / (double) its;
//                    double py = y + (j + 0.5D) / (double) its;
//                    double pz = z + (k + 0.5D) / (double) its;
//                    int random = RANDOM.nextInt(6);
//                    EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, px - x - 0.5D, py - y - 0.5D, pz - z - 0.5D, block, random, state);
//                    fx.setParticleIcon(block.getIcon(world, x, y, z, 0));
//                    if (callback != null)
//                        callback.addDestroyEffects(fx, world, pos, state);
//                    manager.addEffect(fx.func_174845_l());
//                }
//            }
//        }
        return true;
    }

}
