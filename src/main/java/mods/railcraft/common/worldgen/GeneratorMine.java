/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.util.misc.Predicates;
import mods.railcraft.common.worldgen.NoiseGen.NoiseGenSimplex;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class GeneratorMine extends Generator {
    private static final EnumSet<BiomeDictionary.Type> RICH_BIOMES = EnumSet.of(MOUNTAIN, MESA, HILLS);
    private static final boolean SKY_GEN = false;
    private static final Predicate<IBlockState> STONE_TEST = SKY_GEN ? Predicates.alwaysTrue() : GenTools.STONE;
    @Nullable
    private final WorldGenerator poorGen;
    @Nullable
    private final WorldGenerator normGen;
    private final int yLevel, yRange, noiseSeed;

    private final Map<World, NoiseGen> cloudMap = new MapMaker().weakKeys().makeMap();
    private final Map<World, NoiseGen> veinMap = new MapMaker().weakKeys().makeMap();

    protected GeneratorMine(EventType eventType, Metal metal, int density, int yLevel, int yRange, int noiseSeed) {
        super(getGen(metal.getState(Metal.Form.POOR_ORE), density), getGen(metal.getState(Metal.Form.ORE), density));
        this.yLevel = SKY_GEN ? 100 + yLevel : yLevel;
        this.yRange = yRange;
        this.noiseSeed = noiseSeed;
        poorGen = generators[0];
        normGen = generators[1];
    }

    @Nullable
    private static WorldGenerator getGen(@Nullable IBlockState ore, int density) {
        WorldGenerator gen;
        if (ore == null)
            gen = null;
        else if (density >= 4)
            gen = new WorldGenMinable(ore, density, STONE_TEST::test);
        else
            gen = new WorldGenSmallDeposits(ore, density, STONE_TEST);
        return gen;
    }

    @Override
    public void generate(World world, Random rand, BlockPos targetPos, Biome biome) {
        int worldX = targetPos.getX();
        int worldZ = targetPos.getZ();

        boolean generated = attemptGen(world, rand, worldX, worldZ, biome, 16);
        if (generated)
            attemptGen(world, rand, worldX, worldZ, biome, 200);
    }

    private long getNoiseSeed(World world) {
        long seed = world.getSeed();
        seed += world.provider.getDimension();
        seed += noiseSeed;
        return seed;
    }

    private NoiseGen getCloudNoise(World world) {
//        NoiseGen noise = cloudMap.get(world);
        NoiseGen noise = null;
        if (noise == null) {
            noise = new NoiseGenSimplex(new Random(getNoiseSeed(world)), 0.0018);
            cloudMap.put(world, noise);
        }
        return noise;
    }

    private NoiseGen getVeinNoise(World world) {
        NoiseGen noise = veinMap.get(world);
        if (noise == null) {
            noise = new NoiseGenSimplex(new Random(getNoiseSeed(world)), 0.015);
            veinMap.put(world, noise);
        }
        return noise;
    }

    private boolean attemptGen(World world, Random rand, int worldX, int worldZ, Biome biome, int cycles) {
        NoiseGen cloudNoise = getCloudNoise(world);
        NoiseGen veinNoise = getVeinNoise(world);


        boolean rich = false;
        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
        for (BiomeDictionary.Type type : types) {
            if (RICH_BIOMES.contains(type)) {
                rich = true;
                break;
            }
        }

        double fringeArea = 0.7;
        double denseArea = rich ? 0.8 : 0.9;

        boolean generated = false;
        for (int i = 0; i < cycles; i++) {
            int x = worldX + rand.nextInt(16);
            int z = worldZ + rand.nextInt(16);
            double cloudStrength = cloudNoise.noise(x, z);
            if (cloudStrength > fringeArea) {
                int y = yLevel + Math.round((float) rand.nextGaussian() * yRange);
                double veinStrength = veinNoise.noise(x, y, z);
                if (veinStrength >= -0.25F && veinStrength <= 0.25F) {
                    if (cloudStrength > denseArea) {
                        coreGen(world, rand, new BlockPos(x, y, z));
                        generated = true;
                    } else if (rand.nextFloat() > 0.7F) {
                        fringeGen(world, rand, new BlockPos(x, y, z));
                        generated = true;
                    }
                }
            }
        }
        return generated;
    }

    private void coreGen(World world, Random rand, BlockPos pos) {
        WorldGenerator gen;
        if (normGen != null && rand.nextInt(101) < RailcraftConfig.mineStandardOreGenChance())
            gen = normGen;
        else
            gen = poorGen;
        if (gen != null)
            gen.generate(world, rand, pos);
    }

    private void fringeGen(World world, Random rand, BlockPos pos) {
        if (poorGen != null)
            poorGen.generate(world, rand, pos);
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome) {
        if (world.provider.getDimension() != 0)
            return false;
        WorldGenerator gen = Arrays.stream(generators).filter(Predicates.nonNull()).findFirst().orElse(null);
        return TerrainGen.generateOre(world, rand, gen, targetPos, EventType.CUSTOM);
    }
}
