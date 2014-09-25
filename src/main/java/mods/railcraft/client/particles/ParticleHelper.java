/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

    private static final Random rand = new Random();

    @SideOnly(Side.CLIENT)
    public static boolean addHitEffects(World world, Block block, MovingObjectPosition target, EffectRenderer effectRenderer, ParticleHelperCallback callback) {
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        int sideHit = target.sideHit;

        if (block != WorldPlugin.getBlock(world, x, y, z)) return true;
        int meta = world.getBlockMetadata(x, y, z);

        float b = 0.1F;
        double px = x + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F)) + b + block.getBlockBoundsMinX();
        double py = y + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F)) + b + block.getBlockBoundsMinY();
        double pz = z + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F)) + b + block.getBlockBoundsMinZ();

        if (sideHit == 0)
            py = (double) y + block.getBlockBoundsMinY() - (double) b;

        if (sideHit == 1)
            py = (double) y + block.getBlockBoundsMaxY() + (double) b;

        if (sideHit == 2)
            pz = (double) z + block.getBlockBoundsMinZ() - (double) b;

        if (sideHit == 3)
            pz = (double) z + block.getBlockBoundsMaxZ() + (double) b;

        if (sideHit == 4)
            px = (double) x + block.getBlockBoundsMinX() - (double) b;

        if (sideHit == 5)
            px = (double) x + block.getBlockBoundsMaxX() + (double) b;
        EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, 0.0D, 0.0D, 0.0D, block, sideHit, meta);
        fx.setParticleIcon(block.getIcon(world, x, y, z, 0));
        if (callback != null)
            callback.addHitEffects(fx, world, x, y, z, meta);
        effectRenderer.addEffect(fx.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

        return true;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature of how
     * this is invoked, the x/y/z locations are not always guaranteed to host
     * your block. So be sure to do proper sanity checks before assuming that
     * the location is this block.
     *
     * @param world The current world
     * @param block
     * @param x X position to spawn the particle
     * @param y Y position to spawn the particle
     * @param z Z position to spawn the particle
     * @param meta The metadata for the block before it was destroyed.
     * @param effectRenderer A reference to the current effect renderer.
     * @param callback
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public static boolean addDestroyEffects(World world, Block block, int x, int y, int z, int meta, EffectRenderer effectRenderer, ParticleHelperCallback callback) {
        if (block != WorldPlugin.getBlock(world, x, y, z)) return true;
        byte its = 4;
        for (int i = 0; i < its; ++i) {
            for (int j = 0; j < its; ++j) {
                for (int k = 0; k < its; ++k) {
                    double px = x + (i + 0.5D) / (double) its;
                    double py = y + (j + 0.5D) / (double) its;
                    double pz = z + (k + 0.5D) / (double) its;
                    int random = rand.nextInt(6);
                    EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, px - x - 0.5D, py - y - 0.5D, pz - z - 0.5D, block, random, meta);
                    fx.setParticleIcon(block.getIcon(world, x, y, z, 0));
                    if (callback != null)
                        callback.addDestroyEffects(fx, world, x, y, z, meta);
                    effectRenderer.addEffect(fx.applyColourMultiplier(x, y, z));
                }
            }
        }
        return true;
    }

}
