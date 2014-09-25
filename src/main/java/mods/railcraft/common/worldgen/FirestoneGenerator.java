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
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FirestoneGenerator {

    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FIRESTONE", new Class[0], new Object[0]);
    private final WorldGenerator firestone = new WorldGenFirestone();

    @SubscribeEvent
    public void generate(DecorateBiomeEvent.Post event) {

        World world = event.world;
        Random rand = event.rand;
        int worldX = event.chunkX;
        int worldZ = event.chunkZ;

        if (!TerrainGen.decorate(world, rand, worldX, worldZ, EVENT_TYPE))
            return;

        BiomeGenBase biome = world.getBiomeGenForCoords(worldX + 16, worldZ + 16);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.NETHER) && rand.nextDouble() <= 0.1) {
            int x = worldX + rand.nextInt(16);
            int y = 31;
            int z = worldZ + rand.nextInt(16);

            firestone.generate(world, rand, x, y, z);
        }
    }

}
