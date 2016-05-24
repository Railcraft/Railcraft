/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.worldgen.NoiseGen.NoiseGenSimplex;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class PoorOreGenerator {

    private final EventType eventType;
    private final WorldGenerator oreGen;
    private final double scale, denseArea, fringeArea;
    private final int yLevel, yRange, noiseSeed;

    private final Map<World, NoiseGen> noiseMap = new MapMaker().weakKeys().makeMap();

    protected PoorOreGenerator(EventType eventType, EnumOre ore, int density, int yLevel, int yRange, int noiseSeed) {
        this(eventType, ore, 0.0025, 0.85, 0.65, density, yLevel, yRange, noiseSeed);
    }

    protected PoorOreGenerator(EventType eventType, EnumOre ore, double scale, double denseArea, double fringeArea, int density, int yLevel, int yRange, int noiseSeed) {
        this.eventType = eventType;
        this.scale = scale;
        this.denseArea = denseArea;
        this.fringeArea = fringeArea;
        this.yLevel = yLevel;
        this.yRange = yRange;
        this.noiseSeed = noiseSeed;
        if (density >= 4)
            oreGen = new WorldGenMinable(ore.getState(), density, GenTools.STONE);
        else
            oreGen = new WorldGenSmallDeposits(ore.getState(), density, GenTools.STONE);
    }

    @SubscribeEvent
    public void generate(OreGenEvent.Post event) {

        World world = event.world;
        Random rand = event.rand;
        int worldX = event.pos.getX();
        int worldZ = event.pos.getZ();

        if (!TerrainGen.generateOre(world, rand, oreGen, event.pos, eventType))
            return;

        NoiseGen noise = noiseMap.get(world);
        if (noise == null) {
            long seed = world.getSeed();
            seed += world.provider.getDimensionId();
            seed += noiseSeed;
            noise = new NoiseGenSimplex(new Random(seed), scale);
            noiseMap.put(world, noise);
        }

        if (canGen(world, rand, worldX, worldZ))
            for (int i = 0; i < 32; i++) {
                int x = worldX + rand.nextInt(16);
                int z = worldZ + rand.nextInt(16);
                double strength = noise.noise(x, z);
                if (strength > denseArea || (strength > fringeArea && rand.nextFloat() > 0.7)) {
                    int y = yLevel + Math.round((float) rand.nextGaussian() * yRange);
                    oreGen.generate(world, rand, new BlockPos(x, y, z));
                }
            }
    }

    protected boolean canGen(World world, Random rand, int x, int z) {
        return true;
    }

}
