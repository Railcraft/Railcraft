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
import java.util.Random;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

/**
 *
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
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        if (canGen(world, rand, x, z)) {
            int y = world.getTopSolidOrLiquidBlock(x, z) - 3;
            if (WorldPlugin.getBlock(world, x, y, z) == Blocks.dirt) {
                quarry.generate(world, rand, x, y, z);
            }
        }
    }

    private boolean canGen(World world, Random rand, int x, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)) {
            return false;
        }
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SNOWY)) {
            return false;
        }
        return rand.nextDouble() <= 0.025;
    }

}
