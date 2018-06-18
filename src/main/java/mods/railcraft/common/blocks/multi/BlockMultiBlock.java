/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 *
 */
public abstract class BlockMultiBlock extends BlockEntityDelegate {

    protected BlockMultiBlock(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlock(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileMultiBlock createTileEntity(World world, IBlockState state);

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
