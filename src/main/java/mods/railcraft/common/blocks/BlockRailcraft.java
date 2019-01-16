/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockRailcraft extends Block implements IRailcraftBlock {
    protected BlockRailcraft(Material materialIn) {
        super(materialIn);
    }

    protected BlockRailcraft(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    public void markBlockForUpdate(World world, BlockPos pos) {
        if (world != null) {
            IBlockState state = WorldPlugin.getBlockState(world, pos);
            markBlockForUpdate(state, world, pos);
        }
    }

    public void markBlockForUpdate(IBlockState state, World world, BlockPos pos) {
        if (world != null) {
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }
}
