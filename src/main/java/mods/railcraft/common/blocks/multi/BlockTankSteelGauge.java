package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public class BlockTankSteelGauge extends BlockMultiBlock {

    public BlockTankSteelGauge() {
        super(Material.GLASS);
        IBlockState state = getDefaultState();
        for (PropertyEnum<BlockTankIronGauge.RenderState> touch : BlockTankIronGauge.TOUCHES.values()) {
            state = state.withProperty(touch, BlockTankIronGauge.RenderState.DEFAULT);
        }
        setDefaultState(state);
        fullBlock = false;
        lightOpacity = 0;
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        PropertyEnum<?>[] arr = new PropertyEnum<?>[6];
        arr = BlockTankIronGauge.TOUCHES.values().toArray(arr);
        return new BlockStateContainer(this, arr);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankSteelGauge.class;
    }

    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankSteelGauge();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

}
