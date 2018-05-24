package mods.railcraft.common.blocks.multi;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 *
 */
public class BlockTankIronGauge extends BlockMultiBlock {

    public static final Map<EnumFacing, PropertyEnum<RenderState>> TOUCHES;

    static {
        ImmutableMap.Builder<EnumFacing, PropertyEnum<RenderState>> builder = ImmutableMap.builder();
        for (EnumFacing face : EnumFacing.VALUES) {
            builder.put(face, PropertyEnum.create(face.getName(), RenderState.class));
        }
        TOUCHES = builder.build();
    }

    public BlockTankIronGauge() {
        super(Material.GLASS);
        IBlockState state = getDefaultState();
        for (PropertyEnum<RenderState> touch : TOUCHES.values()) {
            state = state.withProperty(touch, RenderState.DEFAULT);
        }
        setDefaultState(state);
        fullBlock = false;
        lightOpacity = 0;
        setHarvestLevel("pickaxe", 1);
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
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankIronGauge();
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
