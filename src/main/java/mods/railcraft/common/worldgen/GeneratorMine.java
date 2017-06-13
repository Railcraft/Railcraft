/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.worldgen.NoiseGen.NoiseGenSimplex;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorMine extends Generator {
    private final Map<World, NoiseGen> cloudMap = new MapMaker().weakKeys().makeMap();
    private final Map<World, NoiseGen> veinMap = new MapMaker().weakKeys().makeMap();
    private final OreGeneratorFactory.BiomeRules biomeRules;
    private final OreGeneratorFactory.GeneratorSettingsMine settings;
    private final WorldGenerator fringeGen, coreGen;

    protected GeneratorMine(Configuration config, OreGeneratorFactory.BiomeRules biomeRules, OreGeneratorFactory.GeneratorSettingsMine settings) {
        this.biomeRules = biomeRules;
        this.settings = settings;

        fringeGen = getGen(settings.fringeOre);
        if (fringeGen == null)
            throw new OreGeneratorFactory.OreConfigurationException(config, "Fringe Ore not found or the block threw an error while generating the blockstate.");
        coreGen = getGen(settings.coreOre);
        if (coreGen == null)
            throw new OreGeneratorFactory.OreConfigurationException(config, "Core Ore not found or the block threw an error while generating the blockstate.");
    }

    @Nullable
    private WorldGenerator getGen(@Nullable IBlockState ore) {
        Predicate<IBlockState> genCheck = state -> RailcraftConfig.isWorldGenEnabled("sky") ? GenTools.AIR_STONE.test(state) : GenTools.STONE.test(state);
        WorldGenerator gen;
        if (ore == null)
            gen = null;
        else if (settings.blockCount >= 4)
            gen = new WorldGenMinable(ore, settings.blockCount, genCheck::test);
        else
            gen = new WorldGenSmallDeposits(ore, settings.blockCount, genCheck);
        return gen;
    }

    @Override
    public void generate(World world, Random rand, BlockPos targetPos, Biome biome) {
        int worldX = targetPos.getX();
        int worldZ = targetPos.getZ();

        boolean rich = biomeRules.isRichBiome(biome);
        NoiseGen cloudNoise = getCloudNoise(world);
        NoiseGen veinNoise = getVeinNoise(world);
        double denseArea = rich ? settings.richLimit : settings.coreLimit;

        boolean generated = attemptGen(world, rand, worldX, worldZ, settings.depth, cloudNoise, veinNoise, 16, denseArea);
        if (generated)
            attemptGen(world, rand, worldX, worldZ, settings.depth, cloudNoise, veinNoise, 200, denseArea);

        if (settings.skyGen) {
            int y = Math.min(100 + settings.depth, world.getHeight() - settings.range * 3);
            generated = attemptGen(world, rand, worldX, worldZ, y, cloudNoise, veinNoise, 16, denseArea);
            if (generated)
                attemptGen(world, rand, worldX, worldZ, y, cloudNoise, veinNoise, 200, denseArea);
        }
    }

    private long getNoiseSeed(World world) {
        long seed = world.getSeed();
        seed += world.provider.getDimension();
        seed += settings.noiseSeed;
        return seed;
    }

    private NoiseGen getCloudNoise(World world) {
        return cloudMap.computeIfAbsent(world, k -> new NoiseGenSimplex(new Random(getNoiseSeed(world)), settings.cloudScale));
    }

    private NoiseGen getVeinNoise(World world) {
        return veinMap.computeIfAbsent(world, k -> new NoiseGenSimplex(new Random(getNoiseSeed(world)), settings.veinScale));
    }

    private boolean attemptGen(World world, Random rand, int worldX, int worldZ, int depth, NoiseGen cloudNoise, NoiseGen veinNoise, int cycles, double denseArea) {
        boolean generated = false;
        for (int i = 0; i < cycles; i++) {
            int x = worldX + rand.nextInt(16);
            int z = worldZ + rand.nextInt(16);
            double cloudStrength = cloudNoise.noise(x, z);
            if (cloudStrength > settings.fringeLimit) {
                int y = depth + Math.round((float) rand.nextGaussian() * settings.range);
                double veinStrength = veinNoise.noise(x, y, z);
                if (veinStrength >= -settings.veinLimit && veinStrength <= settings.veinLimit) {
                    if (cloudStrength > denseArea) {
                        if (rand.nextFloat() <= settings.coreGenChance) {
                            coreGen(world, rand, new BlockPos(x, y, z));
                            generated = true;
                        }
                    } else if (rand.nextFloat() <= settings.fringeGenChance) {
                        fringeGen(world, rand, new BlockPos(x, y, z));
                        generated = true;
                    }
                }
            }
        }
        return generated;
    }

    private void coreGen(World world, Random rand, BlockPos pos) {
        WorldGenerator gen = rand.nextFloat() <= settings.coreOreChance ? coreGen : fringeGen;
        gen.generate(world, rand, pos);
    }

    private void fringeGen(World world, Random rand, BlockPos pos) {
        if (fringeGen != null)
            fringeGen.generate(world, rand, pos);
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome) {
        if (!biomeRules.isValidBiome(biome))
            return false;
        return TerrainGen.generateOre(world, rand, coreGen, targetPos, EventType.CUSTOM);
    }
}
