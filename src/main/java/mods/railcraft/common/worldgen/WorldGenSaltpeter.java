/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import net.minecraft.world.gen.feature.*;
import java.util.Random;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.BlockWorldLogic;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSaltpeter extends WorldGenerator {

    /**
     * The block ID of the ore to be placed using this generator.
     */
    private final Block minableBlock = BlockOre.getBlock();
    private final int minableBlockMeta = EnumOre.SALTPETER.ordinal();

    public WorldGenSaltpeter() {
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (isReplaceable(world, rand, x, y, z)) {
            world.setBlock(x, y, z, minableBlock, minableBlockMeta, 2);

            Block worldLogic = BlockWorldLogic.getBlock();
            if (worldLogic != null) {
                world.setBlock(x, 0, z, worldLogic, 0, 2);
            }
        }

        return true;
    }

    private boolean isReplaceable(World world, Random rand, int x, int y, int z) {
        Block block = WorldPlugin.getBlock(world, x, y, z);
        if (block != Blocks.sand) {
            return false;
        }
        if (SimplexNoise.noise(x * 0.01, z * 0.01) < 0.75) {
            return false;
        }

//        if(world.isAirBlock(x, y + 1, z)){
//            return false;
//        }
        return true;
    }

}
