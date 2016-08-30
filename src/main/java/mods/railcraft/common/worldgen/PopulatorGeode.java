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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;

import java.util.Locale;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PopulatorGeode extends Populator {
    public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "RAILCRAFT_GEODE", new Class[0]);
    public static final int MIN_DEPTH = 16;
    public static final int MIN_FLOOR = 24;
    public static final int GEN_HEIGHT = 60;
    private static PopulatorGeode instance;
    private final WorldGenerator geode = new WorldGenGeode(EnumGeneric.STONE_ABYSSAL.getDefaultState());

    private PopulatorGeode() {
        super(EVENT_TYPE, GEN_HEIGHT);
    }

    public static PopulatorGeode instance() {
        if (instance == null) {
            instance = new PopulatorGeode();
        }
        return instance;
    }

    //TODO: Much testing, oh god
    @Override
    public void populate(World world, Random rand, BlockPos chunkCenterPos) {
        OceanFloor floor = scanOceanFloor(world, chunkCenterPos);
        if (floor.depth >= MIN_DEPTH && floor.floorY >= MIN_FLOOR) {
            int y = 12 + rand.nextInt(floor.floorY - 12);
            geode.generate(world, rand, new BlockPos(chunkCenterPos.getX(), y, chunkCenterPos.getZ()));
        }
    }

    @Override
    public boolean canGen(World world, Random rand, BlockPos pos, Biome biome) {
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WATER)) {
            return false;
        }
        //noinspection ConstantConditions
        if (biome.getBiomeName() == null || biome.getBiomeName().toLowerCase(Locale.ENGLISH).contains("river")) {
            return false;
        }
        return rand.nextDouble() <= 0.3;
    }

    private OceanFloor scanOceanFloor(World world, BlockPos pos) {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        int y = chunk.getTopFilledSegment() + 15;

        int trimmedX = pos.getX() & 15;
        int trimmedZ = pos.getZ() & 15;

        int depth = 0;
        for (; y > 0; --y) {
            IBlockState blockState = chunk.getBlockState(trimmedX, y, trimmedZ);
            if (blockState == Blocks.AIR)
                continue;
            else if (blockState.getMaterial() == Material.WATER)
                depth++;
            else
                break;
        }

        return new OceanFloor(y, depth);
    }

    private class OceanFloor {
        public final int floorY;
        public final int depth;

        public OceanFloor(int floorY, int depth) {
            this.floorY = floorY;
            this.depth = depth;
        }
    }
}
