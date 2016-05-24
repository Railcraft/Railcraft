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
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class QuarryPopulator {

    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RAILCRAFT_QUARRY", new Class[0], new Object[0]);
    private static QuarryPopulator instance;
    private final WorldGenQuarry quarry = new WorldGenQuarry(BlockCube.getBlock(), EnumCube.QUARRIED_STONE.ordinal());

    public static QuarryPopulator instance() {
        if (instance == null) {
            instance = new QuarryPopulator();
        }
        return instance;
    }

    private QuarryPopulator() {
    }

    @SubscribeEvent
    public void generate(PopulateChunkEvent.Pre event) {
        if (!TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, EVENT_TYPE)) {
            return;
        }
        generateQuarry(event.world, event.rand, event.chunkX, event.chunkZ);
    }

    public void generateQuarry(World world, Random rand, int chunkX, int chunkZ) {
        BlockPos chunkCenterPos = new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8);
        if (canGen(world, rand, chunkCenterPos)) {
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(chunkCenterPos).down(3);
            if (WorldPlugin.isBlockAt(world, surfacePos, Blocks.dirt)) {
                quarry.generate(world, rand, surfacePos);
            }
        }
    }

    private boolean canGen(World world, Random rand, BlockPos pos) {
        BiomeGenBase biome = world.getBiomeGenForCoords(pos);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)) {
            return false;
        }
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SNOWY)) {
            return false;
        }
        return rand.nextDouble() <= 0.025;
    }

}
