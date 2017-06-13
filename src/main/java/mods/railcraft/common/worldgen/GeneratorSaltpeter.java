/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorSaltpeter extends Generator {

    //    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "SALTPETER", new Class[0], new Object[0]);
    private final WorldGenerator generator = new WorldGenSaltpeter();

    @Override
    public void generate(World world, Random rand, BlockPos targetPos, Biome biome) {
        int worldX = targetPos.getX();
        int worldZ = targetPos.getZ();
        for (int i = 0; i < 64; i++) {
            int x = worldX + rand.nextInt(16);
            int z = worldZ + rand.nextInt(16);
            BlockPos topBlock = world.getTopSolidOrLiquidBlock(new BlockPos(x, 50, z)).down(1 + (rand.nextInt(100) == 0 ? 0 : 1));
            if (topBlock.getY() < 50 || topBlock.getY() > 100)
                continue;
            generator.generate(world, rand, topBlock);
        }
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome) {
        if (world.provider.getDimension() != 0)
            return false;
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY))
            return false;
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY))
            return false;
        return !biome.canRain() && biome.getTemperature() >= 1.5f && biome.getRainfall() <= 0.1f
                && TerrainGen.generateOre(world, rand, generator, targetPos, OreGenEvent.GenerateMinable.EventType.CUSTOM);
    }

}
