/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;

/**
 *
 */
public abstract class BlockBoilerFirebox<T extends TileBoilerFirebox> extends BlockMultiBlock<T> {

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
}
