package mods.railcraft.common.blocks.multi;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockBoilerTank extends BlockMultiBlock {

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");

    public static final AxisAlignedBB CORE = new AxisAlignedBB(0.0625f,0,0.0625f, 0.9375f,1, 0.9375f);

    protected BlockBoilerTank() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getBlockState().getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBoilerTank) {
            IBlockState actualState = getActualState(state, world, pos);
            AxisAlignedBB bb = CORE;
            if (actualState.getValue(NORTH))
                bb = new AxisAlignedBB(bb.minX, bb.minY, 0, bb.maxX, bb.maxY, bb.maxZ);
            if (actualState.getValue(SOUTH))
                bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, 1);
            if (actualState.getValue(WEST))
                bb = new AxisAlignedBB(0, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            if (actualState.getValue(EAST))
                bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, 1, bb.maxY, bb.maxZ);
            return bb;
        }
        return CORE;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(NORTH, SOUTH, EAST, WEST).build();
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