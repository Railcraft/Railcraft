/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenQuarry extends WorldGenStone {

    public WorldGenQuarry(IBlockState stone) {
        super(stone);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
//        Game.log(Level.INFO, "Generating Quarry at {0}, {1}, {2}", x, y, z);
        boolean clearTop = true;
        for (int x = -8; x < 8; x++) {
            for (int z = -8; z < 8; z++) {
                for (int y = 1; y < 4 && y + position.getY() < world.getActualHeight() - 1; y++) {
                    int distSq = x * x + z * z;
                    if (distSq <= DISTANCE_OUTER_SQ) {
                        IBlockState existingState = WorldPlugin.getBlockState(world, position.add(x, y, z));
                        if (isLiquid(existingState)) {
                            clearTop = false;
                            break;
                        }
                    }
                }
            }
        }
        if (clearTop)
            for (int x = -8; x < 8; x++) {
                for (int z = -8; z < 8; z++) {
                    for (int y = 1; y < 4 && y + position.getY() < world.getActualHeight() - 1; y++) {
                        int distSq = x * x + z * z;
                        if (distSq <= DISTANCE_OUTER_SQ) {
                            BlockPos targetPos = position.add(x, y, z);
                            IBlockState existingState = WorldPlugin.getBlockState(world, targetPos);
                            if (!placeAir(existingState, world, rand, targetPos))
                                break;
                        }
                    }
                }
            }
        for (int x = -8; x < 8; x++) {
            for (int z = -8; z < 8; z++) {
                for (int y = -8; y < 1 && y + position.getY() < world.getActualHeight() - 1; y++) {
                    int distSq = x * x + z * z + y * y;
                    if (distSq <= DISTANCE_OUTER_SQ) {
                        BlockPos targetPos = position.add(x, y, z);
                        IBlockState existingState = WorldPlugin.getBlockState(world, targetPos);
                        replaceStone(existingState, world, rand, targetPos);
                    }
                }
            }
        }

        return true;
    }

}
