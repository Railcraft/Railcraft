package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public class BlockTankSteelValve extends BlockMultiBlock {

    public BlockTankSteelValve() {
        super(Material.ROCK);
        IBlockState state = getDefaultState();
        for (PropertyBool touch : BlockTankIronValve.TOUCHES.values()) {
            state = state.withProperty(touch, false);
        }
        setDefaultState(state);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        PropertyBool[] arr = new PropertyBool[6];
        arr = BlockTankIronValve.TOUCHES.values().toArray(arr);
        return new BlockStateContainer(this, arr);
    }

    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankSteelValve();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(4, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankSteelValve.class;
    }
}
