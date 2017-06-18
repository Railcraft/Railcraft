/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = FeederVariant.class)
public class BlockChargeFeeder extends BlockMachine<FeederVariant> implements IChargeBlock {

    public static final PropertyBool REDSTONE = PropertyBool.create("redstone");

    public static final ChargeDef CHARGE_DEF = new ChargeDef(ConnectType.BLOCK, (world, pos) -> {
        TileEntity tileEntity = WorldPlugin.getBlockTile(world, pos);
        if (tileEntity instanceof TileChargeFeeder) {
            return ((TileChargeFeeder) tileEntity).getChargeBattery();
        }
        //noinspection ConstantConditions
        return null;
    });

    public BlockChargeFeeder() {
        super(true);
        IBlockState defaultState = blockState.getBaseState().withProperty(getVariantProperty(), FeederVariant.IC2).withProperty(REDSTONE, false);
        setDefaultState(defaultState);
        setResistance(10F);
        setHardness(5F);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    @Override
    public void initializeDefinintion() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public void defineRecipes() {
        FeederVariant.IC2.ifAvailable(v ->
                CraftingPlugin.addRecipe(getStack(v),
                        "PPP",
                        "TCT",
                        "PPP",
                        'P', RailcraftItems.PLATE, Metal.TIN,
                        'C', RailcraftItems.CHARGE, ItemCharge.EnumCharge.COIL,
                        'T', RailcraftItems.CHARGE, ItemCharge.EnumCharge.TERMINAL));
    }

    @Nullable
    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_DEF;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(REDSTONE, (meta & 0x8) > 0);
        state = state.withProperty(getVariantProperty(), EnumTools.fromOrdinal(meta & 0x7, FeederVariant.VALUES));
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(getVariantProperty()).ordinal();
        if (state.getValue(REDSTONE))
            meta |= 0x8;
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty(), REDSTONE);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        super.neighborChanged(state, worldIn, pos, blockIn);
        IBlockState newState = detectRedstoneState(state, worldIn, pos);
        if (state != newState)
            WorldPlugin.setBlockState(worldIn, pos, newState);
        TileEntity tileEntity = WorldPlugin.getBlockTile(worldIn, pos);
        if (tileEntity instanceof TileChargeFeeder)
            ((TileChargeFeeder) tileEntity).neighborChanged(newState, worldIn, pos, blockIn);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        return detectRedstoneState(state, worldIn, pos);
    }

    private IBlockState detectRedstoneState(IBlockState state, World worldIn, BlockPos pos) {
        if (Game.isClient(worldIn))
            return state;
        return state.withProperty(REDSTONE, PowerPlugin.isBlockBeingPowered(worldIn, pos));
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(REDSTONE) && rand.nextInt(50) == 25)
            EffectManager.instance.zapEffectSurface(stateIn, worldIn, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        ChargeManager.getNetwork(worldIn).deregisterChargeNode(pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return ChargeManager.getNetwork(worldIn).getGraph(pos).getComparatorOutput();
    }
}
