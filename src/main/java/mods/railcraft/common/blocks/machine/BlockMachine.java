/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.ISubtypedBlock;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A Block designed for blocks with variants defined by tile entities.
 */
public class BlockMachine<V extends Enum<V> & IEnumMachine<V>> extends BlockEntityDelegate<TileMachineBase> implements ISubtypedBlock<V> {
    private VariantData<V> variantData;

    public BlockMachine(Material mat) {
        this(mat, MapColor.GRAY);
    }

    public BlockMachine(Material mat, MapColor mapColor) {
        super(mat, mapColor);
        setResistance(4.5F);
        setHardness(2.0F);
        // TODO: This can't be right
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        setDefaultState(getDefaultState().withProperty(getVariantEnumProperty(), getVariants()[0]));

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        for (IEnumMachine<V> machine : getVariants()) {
            HarvestPlugin.setStateHarvestLevel(machine.getToolClass(), machine);
        }
    }

    @Override
    public VariantData<V> getVariantData() {
        if (variantData == null)
            variantData = ISubtypedBlock.super.getVariantData();
        return variantData;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return convertMetaToState(meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }

//    /**
//     * Returns the default ambient occlusion value based on block opacity
//     */
//    @SideOnly(Side.CLIENT)
//    @Override
//    public float getAmbientOcclusionLightValue(IBlockState state) {
//        return 0.2F;
//    }

    @Override
    public Class<? extends TileMachineBase> getTileClass(IBlockState state) {
        return getVariant(state).getTileClass();
    }

    //TODO: Do we need to do this anymore?
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            if (canSilkHarvest(world, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0) {
                List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, state, 0);
                net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1.0f, true, player);
                for (ItemStack stack : drops) {
                    spawnAsEntity(world, pos, stack);
                }
            } else {
                harvesters.set(player);
                dropBlockAsItem(world, pos, state, 0);
                harvesters.set(null);
            }
        return world.setBlockToAir(pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getDrops(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileMachineBase) {
            drops.addAll(((TileMachineBase) tile).getDrops(fortune));
        } else {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    private List<ItemStack> getBlockDroppedSilkTouch(World world, BlockPos pos, IBlockState state, @SuppressWarnings("SameParameterValue") int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getBlockDroppedSilkTouch(fortune);
        //noinspection deprecation
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        return tile instanceof TileMachineBase && ((TileMachineBase) tile).canSilkHarvest(player);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, world.getBlockState(pos), 0);
        if (drops.isEmpty())
            return super.getPickBlock(state, target, world, pos, player);
        return drops.get(0);
    }

    void initFromItem(World world, BlockPos pos, ItemStack stack) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).initFromItem(stack);
    }

    @Override
    public TileMachineBase createTileEntity(World world, IBlockState state) {
        return getVariant(state).getTileEntity();
    }

    public final List<V> getCreativeList() {
        try {
            Method creativeList = getVariantEnumClass().getMethod("getCreativeList");
            //noinspection unchecked
            return (List<V>) creativeList.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        // leave this as lambda's instead of method references, it breaks otherwise.
        getCreativeList().stream()
                .filter(m -> m.isAvailable())
                .map(m -> m.getStack())
                .filter(s -> !s.isEmpty())
                .forEach(list::add);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getVariant(state).passesLight() ? 0 : 255;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        if (!super.canPlaceBlockOnSide(worldIn, pos, side))
            return false;
        if (needsSupport()) {
            pos = pos.down();
            return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP);
        }
        return true;
    }
}
