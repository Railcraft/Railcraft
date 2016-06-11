/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Created by CovertJaguar on 6/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class Generator {
    private final OreGenEvent.GenerateMinable.EventType eventType;
    protected final WorldGenerator oreGen;

    protected Generator(OreGenEvent.GenerateMinable.EventType eventType, WorldGenerator oreGen) {
        this.eventType = eventType;
        this.oreGen = oreGen;
    }

    @SubscribeEvent
    public final void generate(OreGenEvent.Post event) {
        if (oreGen == null)
            return;

        World world = event.getWorld();
        Random rand = event.getRand();

        if (!TerrainGen.generateOre(world, rand, oreGen, event.getPos(), eventType))
            return;
        BlockPos targetPos = event.getPos();
        Biome biome = world.getBiome(targetPos);
        if (canGen(event.getWorld(), event.getRand(), targetPos, biome)) {
            generate(event.getWorld(), event.getRand(), targetPos);
        }
    }

    public abstract void generate(World world, Random rand, BlockPos targetPos);

    public abstract boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome);
}
