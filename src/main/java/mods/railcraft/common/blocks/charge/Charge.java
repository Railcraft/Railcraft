/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.annotations.Beta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by CovertJaguar on 10/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Charge implements IChargeManager {
    /**
     * The distribution network is the charge network used by standard consumers, wires, tracks, and batteries.
     *
     * This is the only network currently implemented and currently covers all use cases.
     */
    distribution,
    /**
     * The transmission network is the charge network used by low maintenance transmission lines and transformers,
     * consumers should not access this network directly.
     *
     * Not currently implemented.
     */
    @Beta
    transmission,
    /**
     * The rail network is the charge network used by tracks and the carts on them.
     *
     * Not currently implemented.
     */
    @Beta
    rail,
    /**
     * The catenary network is the charge network used by catenaries and the carts below them.
     *
     * Not currently implemented.
     */
    @Beta
    catenary;

    @Override
    public IChargeNetwork network(World world) {
        return manager.network(world);
    }

    /**
     * Entry point for rendering charge related effects.
     */
    public static IZapEffectRenderer effects() {
        return effects;
    }

    public interface IZapEffectRenderer {
        /**
         * Helper method that most blocks can use for spark effects. It has a chance of calling
         * {@link #zapEffectSurface(IBlockState, World, BlockPos)}.
         *
         * The chance is increased if its raining.
         *
         * @param chance Integer value such that chance of sparking is defined by {@code rand.nextInt(chance) == 0}
         *               Most blocks use 50, tracks use 75. Lower numbers means more frequent sparks.
         */
        default void throwSparks(IBlockState state, World world, BlockPos pos, Random rand, int chance) {
        }

        /**
         * Spawns a single spark from a point source.
         *
         * @param source Can be a TileEntity, Entity, BlockPos, or Vec3d
         * @throws IllegalArgumentException If source is of an unexpected type.
         */
        default void zapEffectPoint(World world, Object source) {
        }

        /**
         * Spawns a lot of sparks from a point source.
         *
         * @param source Can be a TileEntity, Entity, BlockPos, or Vec3d
         * @throws IllegalArgumentException If source is of an unexpected type.
         */
        default void zapEffectDeath(World world, Object source) {
        }

        /**
         * Spawns a spark from the surface of each rendered side of a block.
         */
        default void zapEffectSurface(IBlockState stateIn, World worldIn, BlockPos pos) {
        }
    }

    /**
     * User's shouldn't touch this. It's set using reflection by Railcraft.
     */
    @SuppressWarnings("CanBeFinal")
    private IChargeManager manager = new IChargeManager() {
    };

    /**
     * User's shouldn't touch this. It's set using reflection by Railcraft.
     */
    @SuppressWarnings("CanBeFinal")
    private static IZapEffectRenderer effects = new IZapEffectRenderer() {
    };

}
