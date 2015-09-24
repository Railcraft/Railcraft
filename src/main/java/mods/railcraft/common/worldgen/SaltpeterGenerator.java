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

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SaltpeterGenerator {

    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "SALTPETER", new Class[0], new Object[0]);
    private WorldGenerator saltpeter = new WorldGenSaltpeter();

    @SubscribeEvent
    public void generate(OreGenEvent.Post event) {

        World world = event.world;
        Random rand = event.rand;
        int worldX = event.worldX;
        int worldZ = event.worldZ;

        if (!TerrainGen.generateOre(world, rand, saltpeter, worldX, worldZ, EVENT_TYPE))
            return;

        if (canGen(world, rand, worldX, worldZ))
            for (int i = 0; i < 64; i++) {
                int x = worldX + rand.nextInt(16);
                int z = worldZ + rand.nextInt(16);
                int y = world.getTopSolidOrLiquidBlock(x, z) - 1 - (rand.nextInt(100) == 0 ? 0 : 1);
                if (y < 50 || y > 100)
                    continue;
                saltpeter.generate(world, rand, x, y, z);
            }
    }

    private boolean canGen(World world, Random rand, int x, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x + 16, z + 16);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY))
            return false;
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY))
            return false;
        if (biome.canSpawnLightningBolt())
            return false;
        return biome.temperature >= 1.5f && biome.rainfall <= 0.1f;
    }

}
