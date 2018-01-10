package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.EnumMap;

/**
 *
 */
public class BlockTankIronGauge extends BlockMultiBlock {

    public static final EnumMap<EnumFacing, PropertyEnum<RenderState>> TOUCHES = new EnumMap<>(EnumFacing.class);

    static {
        for (EnumFacing face : EnumFacing.VALUES) {
            TOUCHES.put(face, PropertyEnum.create(face.getName(), RenderState.class));
        }
    }

    public BlockTankIronGauge() {
        super(Material.GLASS);
        IBlockState state = getDefaultState();
        for (PropertyEnum<RenderState> touch : TOUCHES.values()) {
            state = state.withProperty(touch, RenderState.DEFAULT);
        }
        setDefaultState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        PropertyEnum<?>[] arr = new PropertyEnum<?>[6];
        arr = TOUCHES.values().toArray(arr);
        return new BlockStateContainer(this, arr);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankIronGauge.class;
    }

    @Override
    public TileMultiBlock createTileEntity(World world, IBlockState state) {
        return new TileTankIronGauge();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 5);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    public enum RenderState implements IStringSerializable {
        DEFAULT,
        TOPMOST,
        MIDDLE,
        BOTTOMMOST,
        TRANSPARENT;

        private final String name = name().toLowerCase();

        @Override
        public String getName() {
            return name;
        }
    }
}
