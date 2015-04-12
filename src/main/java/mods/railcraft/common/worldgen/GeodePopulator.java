/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;
import java.util.Random;

import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

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
        if (!TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, EVENT_TYPE)) {
            return;
        }
        generateGeode(event.world, event.rand, event.chunkX, event.chunkZ);
    }

    public void generateGeode(World world, Random rand, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        if (canGen(world, rand, x, z)) {
            OceanFloor floor = scanOceanFloor(world, x, z);
            if (floor.depth >= MIN_DEPTH && floor.floorY >= MIN_FLOOR) {
                int y = 12 + rand.nextInt(floor.floorY - 12);
                geode.generate(world, rand, x, y, z);
            }
        }
    }

    private boolean canGen(World world, Random rand, int x, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WATER)) {
            return false;
        }
        if (biome.biomeName == null || biome.biomeName.toLowerCase(Locale.ENGLISH).contains("river")) {
            return false;
        }
        return rand.nextDouble() <= 0.3;
    }

    private OceanFloor scanOceanFloor(World world, int x, int z) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int y = chunk.getTopFilledSegment() + 15;

        int trimmedX = x & 15;
        int trimmedZ = z & 15;

        int depth = 0;
        for (; y > 0; --y) {
            Block block = chunk.getBlock(trimmedX, y, trimmedZ);
            if (block == null || block == Blocks.air)
                continue;
            else if (block.getMaterial() == Material.water)
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
