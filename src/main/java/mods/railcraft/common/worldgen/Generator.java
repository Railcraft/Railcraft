/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by CovertJaguar on 6/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class Generator implements IWorldGenerator {
    private final OreGenEvent.GenerateMinable.EventType eventType;
    protected final WorldGenerator[] generators;

    protected Generator(OreGenEvent.GenerateMinable.EventType eventType, WorldGenerator... generators) {
        this.eventType = eventType;
        this.generators = generators;
    }

    @SubscribeEvent
    public final void generate(OreGenEvent.Post event) {
        World world = event.getWorld();
        Random rand = event.getRand();
        _generate(rand, event.getPos(), world);
    }

    @Override
    public final void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        _generate(rand, new BlockPos(chunkX * 16, 0, chunkZ * 16), world);
    }

    private void _generate(Random rand, BlockPos pos, World world) {
        if (ArrayUtils.isEmpty(generators))
            return;
        if (!TerrainGen.generateOre(world, rand, Arrays.stream(generators).filter(Predicates.nonNull()).findFirst().orElse(null), pos, eventType))
            return;

        Biome biome = world.getBiome(pos.add(8, 0, 8));
        if (canGen(world, rand, pos, biome)) {
            generate(world, rand, pos);
        }
    }

    public abstract void generate(World world, Random rand, BlockPos targetPos);

    public abstract boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome);
}
