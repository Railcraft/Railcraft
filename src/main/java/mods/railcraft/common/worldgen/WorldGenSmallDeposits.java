/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.function.Predicate;

/**
 * WorldGenMinable doesn't handle deposits smaller than four blocks very well,
 * so we made our own WorldGenerator for those.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenSmallDeposits extends WorldGenerator {

    private final IBlockState ore;
    private final Predicate<IBlockState> replace;
    private final int blockCount;

    public WorldGenSmallDeposits(IBlockState ore, int blockCount, Predicate<IBlockState> replace) {
        this.ore = ore;
        this.blockCount = blockCount;
        this.replace = replace;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (canGen(world, pos)) {
            placeOre(world, rand, pos);
            return true;
        }
        return false;
    }

    protected boolean canGen(World world, BlockPos pos) {
        return true;
    }

    private void placeOre(World world, Random rand, BlockPos pos) {
        for (int num = 0; num < blockCount; num++) {
            IBlockState blockState = WorldPlugin.getBlockState(world, pos);
            if (blockState.getBlock().isReplaceableOreGen(blockState, world, pos, replace::test))
                WorldPlugin.setBlockState(world, pos, ore, 2);

            pos = pos.offset(EnumFacing.random(rand));
            if (!WorldPlugin.isBlockLoaded(world, pos))
                break;
        }
    }

}
