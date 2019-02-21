/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenFirestone extends WorldGenerator {

    private final IBlockState firestone = EnumOreMagic.FIRESTONE.getDefaultState();

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        for (int yy = y; yy > y - 6; yy--) {
            BlockPos pos = new BlockPos(x, yy, z);
            if (!WorldPlugin.isBlockLoaded(world, pos)) return false;
            Block block = WorldPlugin.getBlock(world, pos);
            if (block != Blocks.LAVA && block != Blocks.FLOWING_LAVA)
                return false;
        }
        int yy = y - 6;
        while (yy > 1) {
            BlockPos pos = new BlockPos(x, yy, z);
            if (!WorldPlugin.isBlockLoaded(world, pos)) return false;
            Block block = WorldPlugin.getBlock(world, pos);
            if (block != Blocks.LAVA && block != Blocks.FLOWING_LAVA)
                break;
            yy--;
        }
        BlockPos pos = new BlockPos(x, yy, z);
        IBlockState blockState = WorldPlugin.getBlockState(world, pos);
        return blockState.getBlock().isReplaceableOreGen(blockState, world, pos, GenTools.NETHERRACK::test) && WorldPlugin.setBlockStateWorldGen(world, pos, firestone);
    }

}
