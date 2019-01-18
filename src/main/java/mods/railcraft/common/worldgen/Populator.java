/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Created by CovertJaguar on 6/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class Populator {
    private final PopulateChunkEvent.Populate.EventType eventType;
    private final int yHeight;

    protected Populator(PopulateChunkEvent.Populate.EventType eventType, int yHeight) {
        this.eventType = eventType;
        this.yHeight = yHeight;
    }

    @SubscribeEvent
    public final void populate(PopulateChunkEvent.Pre event) {
        World world = event.getWorld();
        if (!TerrainGen.populate(event.getGenerator(), world, event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated(), eventType)) {
            return;
        }
        BlockPos chunkCenterPos = new BlockPos(event.getChunkX() * 16 + 8, yHeight, event.getChunkZ() * 16 + 8);
        Biome biome = world.getBiome(chunkCenterPos);
        if (canGen(event.getWorld(), event.getRand(), chunkCenterPos, biome)) {
            populate(event.getWorld(), event.getRand(), chunkCenterPos);
        }
    }

    public abstract void populate(World world, Random rand, BlockPos chunkCenterPos);

    public abstract boolean canGen(World world, Random rand, BlockPos chunkCenterPos, Biome biome);
}
