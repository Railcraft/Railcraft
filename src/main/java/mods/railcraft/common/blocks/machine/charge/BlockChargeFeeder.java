/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.TileManager;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(FeederVariant.class)
public class BlockChargeFeeder extends BlockMachineCharge<FeederVariant> {

    public static final PropertyBool REDSTONE = PropertyBool.create("redstone");

    public BlockChargeFeeder() {
        IBlockState defaultState = blockState.getBaseState().withProperty(getVariantEnumProperty(), FeederVariant.IC2).withProperty(REDSTONE, false);
        setDefaultState(defaultState);
        setResistance(10F);
        setHardness(5F);
    }

    @Override
    public void initializeDefinition() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public void defineRecipes() {
        FeederVariant.IC2.ifAvailable(v ->
                CraftingPlugin.addShapedRecipe(getStack(v),
                        "PPP",
                        "TCT",
                        "PPP",
                        'P', RailcraftItems.PLATE, Metal.TIN,
                        'C', RailcraftItems.CHARGE, ItemCharge.EnumCharge.COIL,
                        'T', RailcraftItems.CHARGE, ItemCharge.EnumCharge.TERMINAL));
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        // TODO: Redesign for bridging distribution/rail networks.
        return Collections.singletonMap(Charge.distribution, getVariant(state).getChargeSpec());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(REDSTONE, (meta & 0x8) > 0);
        state = state.withProperty(getVariantEnumProperty(), EnumTools.fromOrdinal(meta & 0x7, FeederVariant.VALUES));
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(getVariantEnumProperty()).ordinal();
        if (state.getValue(REDSTONE))
            meta |= 0x8;
        return meta;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getStack(getVariant(state));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), REDSTONE);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos neighborPos) {
        super.neighborChanged(state, world, pos, blockIn, neighborPos);
        IBlockState newState = detectRedstoneState(state, world, pos);
        if (state != newState)
            WorldPlugin.setBlockState(world, pos, newState);
        TileManager.forTile(this::getTileClass, state, world, pos)
                .action(TileChargeFeederAdmin.class, t -> t.neighborChanged(newState, world, pos, blockIn));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        return detectRedstoneState(state, worldIn, pos);
    }

    private IBlockState detectRedstoneState(IBlockState state, World worldIn, BlockPos pos) {
        if (Game.isClient(worldIn))
            return state;
        return state.withProperty(REDSTONE, PowerPlugin.isBlockBeingPowered(worldIn, pos));
    }

    @Override
    protected boolean isSparking(IBlockState state) {
        return state.getValue(REDSTONE);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }
}
