/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import com.google.common.base.Predicate;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenSmallDeposits extends WorldGenerator {
    public static final Predicate<IBlockState> STONE = new Predicate<IBlockState>() {
        @Override
        public boolean apply(@Nullable IBlockState input) {
            return input != null && input.getBlock() == Blocks.stone;
        }
    };

    private final IBlockState ore;
    private final Predicate<IBlockState> replace;
    private final int number;

    public WorldGenSmallDeposits(IBlockState ore, int number, Predicate<IBlockState> replace) {
        this.ore = ore;
        this.number = number;
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
        for (int num = 0; num < number; num++) {
            IBlockState blockState = WorldPlugin.getBlockState(world, pos);
            if (!WorldPlugin.isBlockAir(world, pos, blockState) && blockState.getBlock().isReplaceableOreGen(world, pos, replace))
                WorldPlugin.setBlockState(world, pos, ore, 2);

            pos = pos.offset(EnumFacing.random(rand));
            if (!WorldPlugin.isBlockLoaded(world, pos))
                break;
        }
    }

}
