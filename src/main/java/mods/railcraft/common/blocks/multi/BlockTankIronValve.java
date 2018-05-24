package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import java.util.EnumMap;

/**
 *
 */
public class BlockTankIronValve extends BlockMultiBlock {

    public static final EnumMap<EnumFacing, PropertyBool> TOUCHES = new EnumMap<>(EnumFacing.class);

    static {
        for (EnumFacing face : EnumFacing.VALUES) {
            TOUCHES.put(face, PropertyBool.create(face.getName2()));
        }
    }

    public BlockTankIronValve() {
        super(Material.ROCK);
        IBlockState state = getDefaultState();
        for (PropertyBool touch : TOUCHES.values()) {
            state = state.withProperty(touch, false);
        }
        setDefaultState(state);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        PropertyBool[] arr = new PropertyBool[6];
        arr = TOUCHES.values().toArray(arr);
        return new BlockStateContainer(this, arr);
    }

    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankIronValve();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(4, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankIronValve.class;
    }
}
