/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.particles;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.minecraft.util.EnumFacing.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ParticleHelper {

    private static final Random rand = new Random();

    @SideOnly(Side.CLIENT)
    public static boolean addHitEffects(World world, Block block, MovingObjectPosition target, EffectRenderer effectRenderer, ParticleHelperCallback callback) {
        BlockPos pos = target.getBlockPos();
//        int x = pos.getX();
//        int y = pos.getY();
//        int z = pos.getZ();
        
        EnumFacing sideHit = target.sideHit;

//        IBlockState state = world.getBlockState(pos);

//        if (state.getBlock() != block) return true;

//        float b = 0.1F;
//        double px = x + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (b * 2.0F)) + b + block.getBlockBoundsMinX();
//        double py = y + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (b * 2.0F)) + b + block.getBlockBoundsMinY();
//        double pz = z + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (b * 2.0F)) + b + block.getBlockBoundsMinZ();

//        if (sideHit == DOWN)
//            py = y + block.getBlockBoundsMinY() - b;
//
//        if (sideHit == UP)
//            py = y + block.getBlockBoundsMaxY() + b;
//
//        if (sideHit == NORTH)
//            pz = z + block.getBlockBoundsMinZ() - b;
//
//        if (sideHit == SOUTH)
//            pz = z + block.getBlockBoundsMaxZ() + b;
//
//        if (sideHit == WEST)
//            px = x + block.getBlockBoundsMinX() - b;
//
//        if (sideHit == EAST)
//            px = x + block.getBlockBoundsMaxX() + b;
        // TODO 1.8.9 port make sure this si the correct function and it works!
        effectRenderer.addBlockHitEffects(pos, sideHit);
//                
//                
//                new EntityDiggingFX(world, px, py, pz, 0.0D, 0.0D, 0.0D, state);
//        fx.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
//        if (callback != null)
//            callback.addHitEffects(fx, world, pos, state);
//        // func_174846_a: apply colour multiplier
//        effectRenderer.addEffect(fx.func_174846_a(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

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
     * @param effectRenderer A reference to the current effect renderer.
     * @param callback
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public static boolean addDestroyEffects(World world, Block block, BlockPos pos, IBlockState state, EffectRenderer effectRenderer, ParticleHelperCallback callback) {
//        if (block != WorldPlugin.getBlock(world, pos)) return true;
        // TODO 1.8.9 port make sure this si the correct function and it works!
        effectRenderer.addBlockDestroyEffects(pos, state);
//        byte its = 4;
//        for (int i = 0; i < its; ++i) {
//            for (int j = 0; j < its; ++j) {
//                for (int k = 0; k < its; ++k) {
//                    double px = x + (i + 0.5D) / its;
//                    double py = y + (j + 0.5D) / its;
//                    double pz = z + (k + 0.5D) / its;
//                    int random = rand.nextInt(6);
//                    EntityDiggingFX fx = new EntityDiggingFX(world, px, py, pz, px - x - 0.5D, py - y - 0.5D, pz - z - 0.5D, block, random, state);
//                    fx.setParticleIcon(block.getIcon(world, x, y, z, 0));
//                    if (callback != null)
//                        callback.addDestroyEffects(fx, world, pos, state);
//                    effectRenderer.addEffect(fx.func_174845_l());
//                }
//            }
//        }
        return true;
    }

}
