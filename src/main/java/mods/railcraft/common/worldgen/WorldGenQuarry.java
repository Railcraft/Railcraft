/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenQuarry extends WorldGenerator {

    private static final int DISTANCE_OUTER_SQ = 8 * 8;
    private final IBlockState quarryStone;
    public final Set<IBlockState> replaceable = new HashSet<>();

    public WorldGenQuarry(IBlockState quarryStone) {
        this.quarryStone = quarryStone;

        add(Blocks.COAL_ORE);
        add(Blocks.IRON_ORE);
        add(Blocks.GOLD_ORE);
        add(Blocks.DIAMOND_ORE);
        add(Blocks.EMERALD_ORE);
        add(Blocks.LAPIS_ORE);
        add(Blocks.QUARTZ_ORE);
        add(Blocks.REDSTONE_ORE);
        add(Blocks.LIT_REDSTONE_ORE);
        add(Blocks.DIRT);
        add(Blocks.GRAVEL);
        add(Blocks.GRASS);
        add(Blocks.CLAY);

        replaceable.addAll(OreDictPlugin.getOreBlockStates());
    }

    private void add(Block block) {
        replaceable.addAll(block.getBlockState().getValidStates());
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
                        placeStone(existingState, world, rand, targetPos);
                    }
                }
            }
        }

        return true;
    }

    private boolean isLiquid(IBlockState existingState) {
        Block block = existingState.getBlock();
        return block instanceof BlockLiquid || block instanceof IFluidBlock;
    }

    private boolean placeAir(IBlockState existingState, World world, Random rand, BlockPos pos) {
//        if (!world.isBlockLoaded(x, y, z)) {
//            return false;
//        }
        BlockPos up = pos.up();
        if (!WorldPlugin.isBlockAir(world, up))
            return false;
        if (isLiquid(existingState))
            return false;

        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            BlockPos target = up.offset(side);
            if (!WorldPlugin.isBlockLoaded(world, target) || !WorldPlugin.isBlockAir(world, target))
                return false;
        }

        WorldPlugin.setBlockStateWorldGen(world, pos, Blocks.AIR.getDefaultState());
        return true;
    }

    private void placeStone(IBlockState existingState, World world, Random rand, BlockPos pos) {
//        if (!world.isBlockLoaded(x, y, z)) {
//            return;
//        }
        //Removes tall grass
        if (WorldPlugin.isBlockAt(world, pos.up(), Blocks.TALLGRASS))
            WorldPlugin.setBlockStateWorldGen(world, pos, Blocks.AIR.getDefaultState());

        if (isReplaceable(existingState, world, pos))
            WorldPlugin.setBlockStateWorldGen(world, pos, quarryStone);
    }

    private boolean isReplaceable(IBlockState existingState, World world, BlockPos pos) {
        if (existingState.getBlock().isReplaceableOreGen(existingState, world, pos, GenTools.STONE::test))
            return true;
        return replaceable.contains(existingState);
    }

}
