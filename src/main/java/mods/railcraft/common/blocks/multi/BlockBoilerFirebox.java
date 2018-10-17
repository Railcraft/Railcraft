package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;

/**
 *
 */
public abstract class BlockBoilerFirebox extends BlockMultiBlock {

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
