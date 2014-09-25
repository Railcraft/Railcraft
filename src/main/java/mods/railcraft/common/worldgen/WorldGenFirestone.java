/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import java.util.Random;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenFirestone extends WorldGenerator {

    private final Block firestone = BlockOre.getBlock();
    private final int firestoneMeta = EnumOre.FIRESTONE.ordinal();

    public WorldGenFirestone() {
        super();

    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int yy = y; yy > y - 6; yy--) {
            if (!world.blockExists(x, yy, z)) return false;
            Block block = WorldPlugin.getBlock(world, x, yy, z);
            if (block != Blocks.lava && block != Blocks.flowing_lava)
                return false;
        }
        int yy = y - 6;
        while (yy > 1) {
            if (!world.blockExists(x, yy, z)) return false;
            Block block = WorldPlugin.getBlock(world, x, yy, z);
            if (block != Blocks.lava && block != Blocks.flowing_lava)
                break;
            yy--;
        }
        Block block = WorldPlugin.getBlock(world, x, yy, z);
        if (block.isReplaceableOreGen(world, x, yy, z, Blocks.netherrack))
            return world.setBlock(x, yy, z, firestone, firestoneMeta, 2);
        return false;
    }

}
