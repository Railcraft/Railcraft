/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class PopulatorJaded extends Populator {

    public static final int GEN_HEIGHT = 50;
    private static PopulatorJaded instance;
    private final WorldGenJaded gen = new WorldGenJaded(RailcraftBlocks.JADED_STONE.getDefaultState());

    public static PopulatorJaded instance() {
        if (instance == null) {
            instance = new PopulatorJaded();
        }
        return instance;
    }

    private PopulatorJaded() {
        super(EventType.CUSTOM, GEN_HEIGHT);
    }

    @Override
    public void populate(World world, Random rand, BlockPos chunkCenterPos) {
        BlockPos surfacePos = world.getTopSolidOrLiquidBlock(chunkCenterPos).down(3);
        if (WorldPlugin.isMaterialAt(world, surfacePos, Material.GROUND)) {
            gen.generate(world, rand, surfacePos);
        }
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos chunkCenterPos, Biome biome) {
        return BiomeDictionary.hasType(biome, Type.PLAINS) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY) && rand.nextDouble() <= ModuleWorld.config.monolithChance;
    }

}
