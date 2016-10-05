/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockContainerRailcraftSubtyped;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockChargeFeeder extends BlockContainerRailcraftSubtyped implements IChargeBlock {

    public static final PropertyEnum<FeederVariant> VARIANT = PropertyEnum.create("variant", FeederVariant.class);
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
        super(Material.IRON, FeederVariant.class);
        IBlockState defaultState = blockState.getBaseState().withProperty(VARIANT, FeederVariant.IC2).withProperty(REDSTONE, false);
        setDefaultState(defaultState);
        setResistance(10F);
        setHardness(5F);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);

        RailcraftRegistry.register(TileChargeFeederAdmin.class, "charge_feeder_admin");
        RailcraftRegistry.register(TileChargeFeederIC2.class, "charge_feeder_ic2");
    }

    @Override
    public void initializeDefinintion() {
//                HarvestPlugin.setStateHarvestLevel(instance, "crowbar", 0);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);

        ForestryPlugin.addBackpackItem("builder", this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(1, FeederVariant.IC2),
                "PCP",
                "CCC",
                "PCP",
                'P', RailcraftItems.PLATE, Metal.TIN,
                'C', "ingotCopper");
    }

    @Nullable
    @Override
    public ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_DEF;
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(VARIANT, (FeederVariant) variant);
        }
        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(REDSTONE, (meta & 0x8) > 0);
        state = state.withProperty(VARIANT, EnumTools.fromOrdinal(meta & 0x7, FeederVariant.VALUES));
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(VARIANT).ordinal();
        if (state.getValue(REDSTONE))
            meta |= 0x8;
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, REDSTONE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(VARIANT) == FeederVariant.IC2)
            return new TileChargeFeederIC2();
        else if (state.getValue(VARIANT) == FeederVariant.ADMIN)
            return new TileChargeFeederAdmin();
        return null;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
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
            EffectManager.instance.sparkEffectSurface(stateIn, worldIn, pos);
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

    public enum FeederVariant implements IVariantEnumBlock {

        IC2,
        ADMIN;
        public static final FeederVariant[] VALUES = values();
        private final String name;

        FeederVariant() {
            name = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public IRailcraftBlockContainer getContainer() {
            return RailcraftBlocks.CHARGE_FEEDER;
        }

        @Override
        public Tuple<Integer, Integer> getTextureDimensions() {
            return new Tuple<>(2, 1);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
