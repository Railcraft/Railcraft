package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockBoilerTank extends BlockMultiBlock {

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");

    protected BlockBoilerTank() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getBlockState().getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(NORTH, SOUTH, EAST, WEST).build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity t = worldIn.getTileEntity(pos);
        if (!(t instanceof TileBoilerTank))
            return state;
        MultiBlockPattern pattern = ((TileBoilerTank) t).getCurrentPattern();
        if (pattern == null)
            return state;
        BlockPos patternPos = ((TileBoilerTank) t).getPatternPosition();
        if (patternPos == null)
            return state;
        char marker = ((TileBoilerTank) t).getPatternMarker();
        if (marker == 'O')
            return state;
        state = state
                .withProperty(NORTH, pattern.getPatternMarker(patternPos.offset(EnumFacing.NORTH)) == marker)
                .withProperty(SOUTH, pattern.getPatternMarker(patternPos.offset(EnumFacing.SOUTH)) == marker)
                .withProperty(EAST, pattern.getPatternMarker(patternPos.offset(EnumFacing.EAST)) == marker)
                .withProperty(WEST, pattern.getPatternMarker(patternPos.offset(EnumFacing.WEST)) == marker);
        return state;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}