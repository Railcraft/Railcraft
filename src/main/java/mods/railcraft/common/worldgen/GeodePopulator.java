/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeodePopulator {
    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RAILCRAFT_GEODE", new Class[0], new Object[0]);
    public static final int MIN_DEPTH = 16;
    public static final int MIN_FLOOR = 24;
    private static GeodePopulator instance;
    private final WorldGenerator geode = new WorldGenGeode(BlockCube.getBlock(), EnumCube.ABYSSAL_STONE.ordinal());

    private GeodePopulator() {
    }

    public static GeodePopulator instance() {
        if (instance == null) {
            instance = new GeodePopulator();
        }
        return instance;
    }

    @SubscribeEvent
    public void generate(PopulateChunkEvent.Pre event) {
        if (!TerrainGen.populate(event.getGen(), event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated(), EVENT_TYPE)) {
            return;
        }
        generateGeode(event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ());
    }

    //TODO: Much testing, oh god
    public void generateGeode(World world, Random rand, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        BlockPos target = new BlockPos(x, 60, z);
        if (canGen(world, rand, target)) {
            OceanFloor floor = scanOceanFloor(world, target);
            if (floor.depth >= MIN_DEPTH && floor.floorY >= MIN_FLOOR) {
                int y = 12 + rand.nextInt(floor.floorY - 12);
                geode.generate(world, rand, new BlockPos(x, y, z));
            }
        }
    }

    private boolean canGen(World world, Random rand, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WATER)) {
            return false;
        }
        //noinspection ConstantConditions
        if (biome.getBiomeName() == null || biome.getBiomeName().toLowerCase(Locale.ENGLISH).contains("river")) {
            return false;
        }
        return rand.nextDouble() <= 0.3;
    }

    private OceanFloor scanOceanFloor(World world, BlockPos pos) {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        int y = chunk.getTopFilledSegment() + 15;

        int trimmedX = pos.getX() & 15;
        int trimmedZ = pos.getZ() & 15;

        int depth = 0;
        for (; y > 0; --y) {
            IBlockState blockState = chunk.getBlockState(trimmedX, y, trimmedZ);
            if (blockState == Blocks.AIR)
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
