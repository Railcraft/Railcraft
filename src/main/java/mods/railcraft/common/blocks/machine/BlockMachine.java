/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockMachine<M extends Enum<M> & IEnumMachine<M>> extends BlockContainer implements IPostConnection, ColorPlugin.IColoredBlock, IRailcraftBlock {

    private final MachineProxy<M> proxy;
    private final BlockStateContainer myBlockState;

    public BlockMachine(MachineProxy<M> proxy, Boolean opaque) {
        super(Material.ROCK);
        setResistance(4.5F);
        setHardness(2.0F);
        // TODO: This can't be right
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        this.proxy = proxy;
        this.myBlockState = new BlockStateContainer(this, proxy.getVariantProperty());
        setDefaultState(myBlockState.getBaseState().withProperty(proxy.getVariantProperty(), proxy.getMetaMap().get(0)));
        this.fullBlock = opaque;

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        lightOpacity = opaque ? 255 : 0;

        for (IEnumMachine<M> machine : proxy.getMetaMap().values()) {
            HarvestPlugin.setStateHarvestLevel(machine.getToolClass(), machine);
        }
    }

    @Override
    public void finalizeDefinition() {
        ColorPlugin.instance.register(this, this);
    }

    @Override
    public BlockStateContainer getBlockState() {
        return myBlockState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return proxy.getMetaMap().inverse().get(state.getValue(proxy.getVariantProperty()));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(proxy.getVariantProperty(), proxy.getMetaMap().get(meta));
    }

    /**
     * Returns the default ambient occlusion value based on block opacity
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1;
    }

    public MachineProxy<M> getMachineProxy() {
        return proxy;
    }

    public IEnumMachine<M> getMachineType(IBlockState state) {
        return state.getValue(proxy.getVariantProperty());
    }

    @Override
    public IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) -> {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileMachineBase)
                return ((TileMachineBase) tile).colorMultiplier();
            return EnumColor.WHITE.getHexColor();
        };
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileMachineBase && ((TileMachineBase) tile).recolourBlock(color);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof TileMachineBase && ((TileMachineBase) tile).blockActivated(playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileMachineBase && ((TileMachineBase) tile).rotateBlock(axis);
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getValidRotations();
        return super.getValidRotations(world, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).randomDisplayTick(rand);
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        return !(tile instanceof TileMachineBase) || ((TileMachineBase) tile).isSideSolid(side);
    }

    //TODO: Do we need to do this anymore?
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
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

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getDrops(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    private List<ItemStack> getBlockDroppedSilkTouch(World world, BlockPos pos, IBlockState state, @SuppressWarnings("SameParameterValue") int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getBlockDroppedSilkTouch(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileMachineBase && ((TileMachineBase) tile).canSilkHarvest(player);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, world.getBlockState(pos), 0);
        return drops.get(0);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).isPoweringTo(side) ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).canConnectRedstone(side);
        return false;
    }

    void initFromItem(World world, BlockPos pos, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).initFromItem(stack);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockPlacedBy(state, placer, stack);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
        try {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileMachineBase)
                ((TileMachineBase) tile).onNeighborBlockChange(state, neighborBlock);
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()");
            if (Game.IS_DEBUG)
                throw error;
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockAdded();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockRemoval();
//        world.notifyBlocksOfNeighborChange(x + 1, y, z, blockID);
//        world.notifyBlocksOfNeighborChange(x - 1, y, z, blockID);
//        world.notifyBlocksOfNeighborChange(x, y, z + 1, blockID);
//        world.notifyBlocksOfNeighborChange(x, y, z - 1, blockID);
//        world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
//        world.notifyBlocksOfNeighborChange(x, y + 1, z, blockID);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return getMachineType(state).getTileEntity();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (pos.getY() < 0)
            return 0;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getLightValue();
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.addAll(
                // leave this as lambda's instead of method references, it breaks otherwise.
                proxy.getCreativeList().stream()
                        .filter((m) -> m.isAvailable())
                        .map((m1) -> m1.getItem())
                        .collect(Collectors.toList())
        );
    }

    //TODO: is this necessary?
    @Override
    public final boolean isOpaqueCube(IBlockState state) {
        return fullBlock;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getMachineType(WorldPlugin.getBlockState(world, pos)).passesLight() ? 0 : 255;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getResistance(exploder) * 3f / 5f;
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).canCreatureSpawn(type);
        return super.canCreatureSpawn(state, world, pos, type);
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getHardness();
        return super.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return proxy instanceof IComparatorOverride;
    }

    /**
     * Value is provided by the tile entity
     */
    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IComparatorValueProvider)
            return ((IComparatorValueProvider) tile).getComparatorInputOverride(worldIn, pos);
        return 0;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing face) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).connectsToPost(face);
        return ConnectStyle.NONE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return BoundingBoxManager.getCollisionBox(world, pos, getMachineType(world.getBlockState(pos)));
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return BoundingBoxManager.getSelectionBox(world, pos, getMachineType(world.getBlockState(pos)));
    }
}
