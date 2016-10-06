/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PopulatorQuarry extends Populator {

    //    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RAILCRAFT_QUARRY", new Class[0]);
    public static final int GEN_HEIGHT = 50;
    private static PopulatorQuarry instance;
    private final WorldGenQuarry quarry = new WorldGenQuarry(EnumGeneric.STONE_QUARRIED.getDefaultState());

    public static PopulatorQuarry instance() {
        if (instance == null) {
            instance = new PopulatorQuarry();
        }
        return instance;
    }

    private PopulatorQuarry() {
        super(EventType.CUSTOM, GEN_HEIGHT);
    }

    @Override
    public void populate(World world, Random rand, BlockPos chunkCenterPos) {
        BlockPos surfacePos = world.getTopSolidOrLiquidBlock(chunkCenterPos).down(3);
        if (WorldPlugin.isBlockAt(world, surfacePos, Blocks.DIRT)) {
            quarry.generate(world, rand, surfacePos);
        }
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos chunkCenterPos, Biome biome) {
        return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SNOWY) && rand.nextDouble() <= 0.025;
    }

}
