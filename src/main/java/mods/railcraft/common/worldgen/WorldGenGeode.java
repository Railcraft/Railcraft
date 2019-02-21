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
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenGeode extends WorldGenerator {

    private static final int DISTANCE_OUTER_SQ = 8 * 8;
    private static final int DISTANCE_ORE_SQ = 5 * 5;
    private static final int DISTANCE_INNER_SQ = 4 * 4;
    private final IBlockState geodeStone;
    public final Set<IBlockState> replaceable = new HashSet<>();
    public final Set<Block> banned = new HashSet<>();
    private final Block blockOre;

    public WorldGenGeode(IBlockState geodeStone) {
        this.geodeStone = geodeStone;

        add(Blocks.COAL_ORE);
        add(Blocks.IRON_ORE);
        add(Blocks.GOLD_ORE);
        add(Blocks.DIAMOND_ORE);
        add(Blocks.EMERALD_ORE);
        add(Blocks.LAPIS_ORE);
        add(Blocks.QUARTZ_ORE);
        add(Blocks.REDSTONE_ORE);
        add(Blocks.LIT_REDSTONE_ORE);

        replaceable.addAll(OreDictPlugin.getOreBlockStates());

        blockOre = RailcraftBlocks.ORE.block();

        banned.add(Blocks.PRISMARINE);
        banned.add(Blocks.STONEBRICK);
        banned.add(Blocks.END_PORTAL);
        banned.add(Blocks.END_PORTAL_FRAME);
        banned.add(Blocks.STONE_BRICK_STAIRS);
        banned.add(Blocks.STONE_STAIRS);
    }

    private void add(Block block) {
        replaceable.addAll(block.getBlockState().getValidStates());
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        for (int x = -8; x < 8; x++) {
            for (int y = -8; y < 8; y++) {
                for (int z = -8; z < 8; z++) {
                    int distSq = x * x + y * y + z * z;
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState existingState = WorldPlugin.getBlockState(world, targetPos);
                    if (distSq <= DISTANCE_INNER_SQ)
                        placeAir(existingState, world, rand, targetPos);
                    else if (distSq <= DISTANCE_OUTER_SQ)
                        placeStone(existingState, world, rand, targetPos);
                    existingState = WorldPlugin.getBlockState(world, targetPos);
                    if (blockOre != null && distSq > DISTANCE_INNER_SQ && distSq <= DISTANCE_ORE_SQ)
                        placeOre(existingState, world, rand, targetPos);
                }
            }
        }
        return true;
    }

    private void placeAir(IBlockState existingState, World world, Random rand, BlockPos pos) {
        if (isReplaceable(existingState, world, pos))
            WorldPlugin.setBlockStateWorldGen(world, pos, Blocks.AIR.getDefaultState());
    }

    private void placeStone(IBlockState existingState, World world, Random rand, BlockPos pos) {
        if (isReplaceable(existingState, world, pos))
            WorldPlugin.setBlockStateWorldGen(world, pos, geodeStone);
    }

    private void placeOre(IBlockState existingState, World world, Random rand, BlockPos pos) {
        if (existingState == geodeStone) {
            double chance = rand.nextDouble();
            IBlockState oreState = null;
            if (chance <= 0.004 && EnumOre.DARK_DIAMOND.isEnabled())
                oreState = EnumOre.DARK_DIAMOND.getDefaultState();
            else if (chance <= 0.008 && EnumOre.DARK_EMERALD.isEnabled())
                oreState = EnumOre.DARK_EMERALD.getDefaultState();
            else if (chance <= 0.02 && EnumOre.DARK_LAPIS.isEnabled())
                oreState = EnumOre.DARK_LAPIS.getDefaultState();
            if (oreState != null)
                WorldPlugin.setBlockStateWorldGen(world, pos, oreState);
        }
    }

    private boolean isReplaceable(IBlockState existingState, World world, BlockPos pos) {
        Block existing = existingState.getBlock();
        if (banned.contains(existing))
            return false;
        if (existing.isReplaceableOreGen(existingState, world, pos, GenTools.STONE::test))
            return true;
        if (existing.isReplaceableOreGen(existingState, world, pos, GenTools.DIRT::test))
            return true;
        if (existing.isReplaceableOreGen(existingState, world, pos, GenTools.GRAVEL::test))
            return true;
        if (existing.isReplaceableOreGen(existingState, world, pos, GenTools.SAND::test))
            return true;
        if (existingState.getMaterial() == Material.WATER)
            return true;
        if (existingState.getMaterial() == Material.ROCK)
            return true;
        if (existingState.getMaterial() == Material.GROUND)
            return true;
        return replaceable.contains(existingState);
    }

}
