/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldGenSulfur extends WorldGenSmallDeposits {

    private static final int AMOUNT = 10;

    public WorldGenSulfur() {
        super(EnumOre.SULFUR.getDefaultState(), AMOUNT, GenTools.STONE);
    }

    @Override
    protected boolean canGen(World world, BlockPos pos) {
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (WorldPlugin.isBlockLoaded(world, pos.offset(side))) {
                Block block = WorldPlugin.getBlock(world, pos);
                if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
                    return true;
            }
        }
        for (int j = 0; j < 4; j++) {
            Block block = WorldPlugin.getBlock(world, pos.down(j));

            if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
                return true;
            else if (block != Blocks.AIR)
                return false;
        }
        return false;
    }

}
