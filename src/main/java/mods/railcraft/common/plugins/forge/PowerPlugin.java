/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class PowerPlugin {

    public static final int NO_POWER = 0;
    public static final int FULL_POWER = 15;

    public static boolean isBlockBeingPowered(World world, BlockPos pos) {
        return world.isBlockPowered(pos);
//        return world.isBlockIndirectlyGettingPowered(pos) > 0;
    }

    public static boolean isBlockBeingPowered(World world, BlockPos pos, EnumFacing from) {
        return world.isSidePowered(pos.offset(from), from);
    }

    public static int getBlockPowerLevel(World world, BlockPos pos, EnumFacing from) {
        return world.getRedstonePower(pos.offset(from), from);
    }

    public static boolean isBlockBeingPoweredByRepeater(World world, BlockPos pos, EnumFacing from) {
        Block block = WorldPlugin.getBlock(world, pos.offset(from));
        return block == Blocks.POWERED_REPEATER && isBlockBeingPowered(world, pos, from);
    }

    public static boolean isBlockBeingPoweredByRepeater(World world, BlockPos pos) {
        return Arrays.stream(EnumFacing.HORIZONTALS).anyMatch(side -> isBlockBeingPoweredByRepeater(world, pos, side));
    }

    public static boolean isRedstonePowered(World world, BlockPos pos) {
        return Arrays.stream(EnumFacing.VALUES)
                .anyMatch(side -> isRedstonePowering(world, pos, 0, side)
                        || isRedstonePowering(world, pos, -1, side));
    }

    private static boolean isRedstonePowering(World world, BlockPos pos, int yOffset, EnumFacing side) {
        BlockPos wirePos = pos.up(yOffset).offset(side);
        IBlockState state = WorldPlugin.getBlockState(world, wirePos);
        return state.getWeakPower(world, wirePos, side) > 0;
    }

}
