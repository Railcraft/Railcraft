/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.worldgen.OreGeneratorFactory.BiomeRules;
import mods.railcraft.common.worldgen.OreGeneratorFactory.DimensionRules;
import mods.railcraft.common.worldgen.OreGeneratorFactory.GeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public final class GeneratorDiffuse extends RuledGenerator {

    private final WorldGenMinable mineable;
    private final GeneratorSettings settings;

    public GeneratorDiffuse(DimensionRules dimensionRules, BiomeRules biomeRules, GeneratorSettings settings) {
        super(dimensionRules, biomeRules);
        this.mineable = new WorldGenMinable(settings.coreOre, settings.blockCount);
        this.settings = settings;
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome) {
        return super.canGen(world, rand, targetPos, biome) && TerrainGen.generateOre(world, rand, mineable, targetPos, EventType.CUSTOM);
    }

    @Override
    public void generate(World world, Random rand, BlockPos targetPos, Biome biome) {
        genStandardOre(world, targetPos.add(-8, 0, -8), rand, settings.blockCount, mineable, settings.depth - settings.range, settings.depth + settings.range);
    }

    // Copied from vanilla populator
    protected static void genStandardOre(World worldIn, BlockPos edgePos, Random random, int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
        if (maxHeight < minHeight) {
            int i = minHeight;
            minHeight = maxHeight;
            maxHeight = i;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }

        for (int j = 0; j < blockCount; ++j) {
            BlockPos blockpos = edgePos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
            generator.generate(worldIn, random, blockpos);
        }
    }
}
