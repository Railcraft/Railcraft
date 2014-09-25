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
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.util.misc.MiscTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenSmallDeposits extends WorldGenerator {

    private final Block ore, replace;
    private final int meta, number;

    public WorldGenSmallDeposits(Block ore, int meta, int number, Block replace) {
        this.ore = ore;
        this.meta = meta;
        this.number = number;
        this.replace = replace;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (canGen(world, x, y, z)) {
            placeOre(world, rand, x, y, z);
            return true;
        }
        return false;
    }

    protected boolean canGen(World world, int x, int y, int z) {
        return true;
    }

    private void placeOre(World world, Random rand, int x, int y, int z) {
        for (int num = 0; num < number; num++) {
            Block block = WorldPlugin.getBlock(world, x, y, z);
            if (block != null && block.isReplaceableOreGen(world, x, y, z, replace))
                world.setBlock(x, y, z, ore, meta, 2);

            ForgeDirection dir = ForgeDirection.getOrientation(rand.nextInt(6));

            x = MiscTools.getXOnSide(x, dir);
            y = MiscTools.getYOnSide(y, dir);
            z = MiscTools.getZOnSide(z, dir);

            if (!world.blockExists(x, y, z))
                break;
        }
    }

}
