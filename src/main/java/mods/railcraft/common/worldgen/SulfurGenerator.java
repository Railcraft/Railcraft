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
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SulfurGenerator {

    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "SULFUR", new Class[0], new Object[0]);
    private final WorldGenerator sulfur = new WorldGenSulfur();

    @SubscribeEvent
    public void generate(OreGenEvent.Post event) {

        World world = event.world;
        Random rand = event.rand;
        int worldX = event.worldX;
        int worldZ = event.worldZ;

        if (!TerrainGen.generateOre(world, rand, sulfur, worldX, worldZ, EVENT_TYPE))
            return;

        BiomeGenBase biome = world.getBiomeGenForCoords(worldX + 16, worldZ + 16);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN))
            for (int i = 0; i < 90; i++) {
                int x = worldX + rand.nextInt(16);
                int y = 6 + rand.nextInt(10);
                int z = worldZ + rand.nextInt(16);

                sulfur.generate(world, rand, x, y, z);
            }
    }

}
