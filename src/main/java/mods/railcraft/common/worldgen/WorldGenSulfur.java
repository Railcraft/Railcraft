/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenSulfur extends WorldGenSmallDeposits {

    private static final int AMOUNT = 10;

    public WorldGenSulfur() {
        super(BlockOre.getBlock(), EnumOre.SULFUR.ordinal(), AMOUNT, Blocks.stone);
    }

    @Override
    protected boolean canGen(World world, int x, int y, int z) {
        for (int side = 2; side < 6; side++) {
            ForgeDirection s = ForgeDirection.getOrientation(side);
            int i = MiscTools.getXOnSide(x, s);
            int j = MiscTools.getYOnSide(y, s);
            int k = MiscTools.getZOnSide(z, s);

            if (world.blockExists(i, j, k)) {
                Block block = WorldPlugin.getBlock(world, i, j, k);
                if (block == Blocks.lava || block == Blocks.flowing_lava)
                    return true;
            }
        }
        for (int j = 0; j < 4; j++) {
            Block block = WorldPlugin.getBlock(world, x, y - j, z);

            if (block == Blocks.lava || block == Blocks.flowing_lava)
                return true;
            else if (block != Blocks.air)
                return false;
        }
        return false;
    }

}
