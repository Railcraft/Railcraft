/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.BlockMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Map;

import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;

@SuppressWarnings("unused")
@BlockMeta.Tile(TileEngravingBench.class)
public class BlockEngravingBench extends BlockEntityDelegate<TileEngravingBench> implements IChargeBlock {
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.1);
    public static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class, X, Z);

    public BlockEngravingBench() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setDefaultState(getDefaultState().withProperty(AXIS, Z));
        setTickRandomly(true);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS) == Z ? 1 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, (meta & 1) == 0 ? X : Z);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this));
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        Axis axis = placer.getHorizontalFacing().getAxis();
        return getDefaultState().withProperty(AXIS, axis);
    }
}
