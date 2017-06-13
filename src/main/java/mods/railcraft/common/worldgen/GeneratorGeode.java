/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorGeode extends Generator {
    //    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RAILCRAFT_GEODE", new Class[0]);
    public static final int MIN_DEPTH = 16;
    public static final int MIN_Y = 12;
    public static final int MIN_FLOOR = 24;
    private final WorldGenerator generator = new WorldGenGeode(EnumGeneric.STONE_ABYSSAL.getDefaultState());

    @Override
    public void generate(World world, Random rand, BlockPos targetPos, Biome biome) {
        BlockPos geodeCenter = targetPos.add(8, 0, 8);
        OceanFloor floor = scanOceanFloor(world, geodeCenter);
        if (floor.depth >= MIN_DEPTH && floor.floorY >= MIN_FLOOR) {
            int deviation = MIN_Y + Math.round(Math.abs((float) rand.nextGaussian()) * (floor.floorY - MIN_Y) * 0.5F);
//            Game.log(Level.INFO, "Deviation from floor: {0}", deviation - floor.floorY);
            int y = Math.min(floor.floorY, deviation);
            generator.generate(world, rand, new BlockPos(geodeCenter.getX(), y, geodeCenter.getZ()));
        }
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos pos, Biome biome) {
        return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.OCEAN) && rand.nextDouble() <= 0.2;
    }

    private OceanFloor scanOceanFloor(World world, BlockPos pos) {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        int y = chunk.getTopFilledSegment() + 15;

        int trimmedX = pos.getX() & 15;
        int trimmedZ = pos.getZ() & 15;

        int depth = 0;
        for (; y > 0; --y) {
            IBlockState blockState = chunk.getBlockState(trimmedX, y, trimmedZ);
            if (blockState.getBlock() == Blocks.AIR)
                continue;
            else if (blockState.getMaterial() == Material.WATER)
                depth++;
            else
                break;
        }

        return new OceanFloor(y, depth);
    }

    private class OceanFloor {
        public final int floorY;
        public final int depth;

        public OceanFloor(int floorY, int depth) {
            this.floorY = floorY;
            this.depth = depth;
        }
    }
}
