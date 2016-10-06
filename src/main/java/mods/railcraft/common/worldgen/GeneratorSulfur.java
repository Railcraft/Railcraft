/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorSulfur {

    //    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "SULFUR", new Class[0], new Object[0]);
    private final WorldGenerator sulfur = new WorldGenSulfur();

    @SubscribeEvent
    public void generate(OreGenEvent.Post event) {

        World world = event.getWorld();
        Random rand = event.getRand();
        BlockPos pos = event.getPos();

        if (!TerrainGen.generateOre(world, rand, sulfur, pos, EventType.CUSTOM))
            return;

        Biome biome = world.getBiome(pos.add(8, 0, 8));
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN))
            for (int i = 0; i < 90; i++) {
                int x = pos.getX() + rand.nextInt(16);
                int y = 6 + rand.nextInt(10);
                int z = pos.getZ() + rand.nextInt(16);

                sulfur.generate(world, rand, new BlockPos(x, y, z));
            }
    }

}
