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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Map;
import java.util.Random;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.worldgen.NoiseGen.NoiseGenSimplex;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class PoorOreGenerator {

    private final EventType eventType;
    private final WorldGenerator oreGen;
    private final double scale, denseArea, fringeArea;
    private final int yLevel, yRange, noiseSeed;

    private final Map<World, NoiseGen> noiseMap = new MapMaker().weakKeys().makeMap();

    public PoorOreGenerator(EventType eventType, EnumOre ore, int density, int yLevel, int yRange, int noiseSeed) {
        this(eventType, ore, 0.0025, 0.85, 0.65, density, yLevel, yRange, noiseSeed);
    }

    public PoorOreGenerator(EventType eventType, EnumOre ore, double scale, double denseArea, double fringeArea, int density, int yLevel, int yRange, int noiseSeed) {
        this.eventType = eventType;
        this.scale = scale;
        this.denseArea = denseArea;
        this.fringeArea = fringeArea;
        this.yLevel = yLevel;
        this.yRange = yRange;
        this.noiseSeed = noiseSeed;
        if (density >= 4)
            oreGen = new WorldGenMinable(BlockOre.getBlock(), ore.ordinal(), density, Blocks.stone);
        else
            oreGen = new WorldGenSmallDeposits(BlockOre.getBlock(), ore.ordinal(), density, Blocks.stone);
    }

    @SubscribeEvent
    public void generate(OreGenEvent.Post event) {

        World world = event.world;
        Random rand = event.rand;
        int worldX = event.worldX;
        int worldZ = event.worldZ;

        if (!TerrainGen.generateOre(world, rand, oreGen, worldX, worldZ, eventType))
            return;

        NoiseGen noise = noiseMap.get(world);
        if (noise == null) {
            long seed = world.getSeed();
            seed += world.provider.dimensionId;
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
                    oreGen.generate(world, rand, x, y, z);
                }
            }
    }

    protected boolean canGen(World world, Random rand, int x, int z) {
        return true;
    }

}
