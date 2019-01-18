/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockWorldLogic extends BlockRailcraft {

    public BlockWorldLogic() {
        super(Material.ROCK);
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        disableStats();

        setTickRandomly(true);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 0);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        world.scheduleBlockUpdate(pos, this, tickRate(world), 0);
        if (MiscTools.RANDOM.nextInt(32) != 0)
            return;
        Block blockOre = RailcraftBlocks.ORE.block();
        if (blockOre == null || !EnumOre.SALTPETER.isEnabled() || !RailcraftConfig.isWorldGenEnabled("saltpeter"))
            return;

        boolean genSurface = rand.nextInt(25) == 0;

        int belowSurfaceY = world.getTopSolidOrLiquidBlock(pos).getY() - 2;

        if (belowSurfaceY < 50 || belowSurfaceY > 100)
            return;

        BlockPos belowSurface = new BlockPos(pos.getX(), belowSurfaceY, pos.getZ());

        Block block = WorldPlugin.getBlock(world, belowSurface);
        if (block != Blocks.SAND)
            return;

        Block above = WorldPlugin.getBlock(world, belowSurface.up());
        if (above != Blocks.SAND)
            return;

        if (!genSurface) {
            Block below = WorldPlugin.getBlock(world, belowSurface.down());
            if (below != Blocks.SAND && below != Blocks.SANDSTONE)
                return;

            int airCount = 0;
            for (EnumFacing side : EnumFacing.HORIZONTALS) {
                boolean isAir = world.isAirBlock(belowSurface.offset(side));
                if (isAir)
                    airCount++;

                if (airCount > 1)
                    return;

                if (isAir)
                    continue;

                block = WorldPlugin.getBlock(world, belowSurface.offset(side));
                if (block != Blocks.SAND && block != Blocks.SANDSTONE && block != blockOre)
                    return;
            }
        }

        world.setBlockState(genSurface ? belowSurface.up() : belowSurface, EnumOre.SALTPETER.getDefaultState());
//        System.out.println("saltpeter spawned");
    }

    @Override
    public int tickRate(World world) {
        return 6000;
    }
}
