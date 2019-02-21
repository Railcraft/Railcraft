/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenSaltpeter extends WorldGenerator {

    /**
     * The block ID of the ore to be placed using this generator.
     */
    private final IBlockState mineableBlock = EnumOre.SALTPETER.getDefaultState();
    private final IBlockState logicBlock = RailcraftBlocks.WORLD_LOGIC.getDefaultState();

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (mineableBlock != null && isReplaceable(world, pos)) {
            WorldPlugin.setBlockStateWorldGen(world, pos, mineableBlock);

            if (logicBlock != null) {
                WorldPlugin.setBlockStateWorldGen(world, new BlockPos(pos.getX(), 0, pos.getZ()), logicBlock);
            }
        }

        return true;
    }

    private boolean isReplaceable(World world, BlockPos pos) {
        Block block = WorldPlugin.getBlock(world, pos);
        if (block != Blocks.SAND) {
            return false;
        }
        return !(SimplexNoise.noise(pos.getX() * 0.01, pos.getZ() * 0.01) < 0.75);
    }

}
