/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

/**
 *
 */
public abstract class BlockBoilerFirebox<T extends TileBoilerFirebox> extends BlockEntityDelegate<T> {

    protected static final IProperty<Boolean> BURNING = PropertyBool.create("burning");

    protected BlockBoilerFirebox() {
        super(Material.ROCK);
        setDefaultState(getDefaultState().withProperty(BURNING, false));
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BURNING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
