/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
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
 * Created by CovertJaguar on 6/25/2022 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class WorldGenStone extends WorldGenerator {
    protected static final int DISTANCE_OUTER_SQ = 8 * 8;
    public final Set<IBlockState> replaceable = new HashSet<>();
    protected final IBlockState stone;

    protected WorldGenStone(IBlockState stone) {
        this.stone = stone;

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

    protected void add(Block block) {
        replaceable.addAll(block.getBlockState().getValidStates());
    }

    protected void replaceStone(IBlockState existingState, World world, Random rand, BlockPos pos) {
//        if (!world.isBlockLoaded(x, y, z)) {
//            return;
//        }
        //Removes tall grass
        if (WorldPlugin.isBlockAt(world, pos.up(), Blocks.TALLGRASS))
            WorldPlugin.setBlockStateWorldGen(world, pos, Blocks.AIR.getDefaultState());

        if (isReplaceable(existingState, world, pos))
            WorldPlugin.setBlockStateWorldGen(world, pos, stone);
    }

    protected boolean placeAir(IBlockState existingState, World world, Random rand, BlockPos pos) {
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

    boolean isReplaceable(IBlockState existingState, World world, BlockPos pos) {
        if (existingState.getBlock().isReplaceableOreGen(existingState, world, pos, GenTools.STONE::test))
            return true;
        return replaceable.contains(existingState);
    }

    protected boolean isLiquid(IBlockState existingState) {
        Block block = existingState.getBlock();
        return block instanceof BlockLiquid || block instanceof IFluidBlock;
    }
}
