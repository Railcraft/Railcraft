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
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Random;

public class BlockMachine<M extends IEnumMachine<M>> extends BlockContainer implements IPostConnection {

    private final IMachineProxy<M> proxy;
    private final int[] metaOpacity;
    private final BlockState altBlockState;

    public BlockMachine(IMachineProxy<M> proxy, boolean opaque, int[] metaOpacity) {
        super(Material.rock);
        setResistance(4.5F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setTickRandomly(true);
        this.proxy = proxy;
        this.altBlockState = new BlockState(this, proxy.getVariantProperty());
        setDefaultState(altBlockState.getBaseState().withProperty(proxy.getVariantProperty(), proxy.getMachine(0)));
        this.fullBlock = opaque;

        this.metaOpacity = metaOpacity;

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        lightOpacity = opaque ? 255 : 0;
    }

    @Override
    public BlockState getBlockState() {
        return this.altBlockState;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return proxy.getMeta(state.getValue(proxy.getVariantProperty()));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(proxy.getVariantProperty(), proxy.getMachine(meta));
    }

    /**
     * Returns the default ambient occlusion value based on block opacity
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue() {
        return 1;
    }

    public IMachineProxy<M> getMachineProxy() {
        return proxy;
    }

    public M getMachineType(IBlockState state) {
        return state.getValue(proxy.getVariantProperty());
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).colorMultiplier();
        return super.colorMultiplier(worldIn, pos, renderPass);
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).recolourBlock(color);
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).blockActivated(playerIn, side);
        return false;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).rotateBlock(axis);
        return false;
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
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).randomDisplayTick(rand);
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).isSideSolid(side);
        return true;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        IBlockState state = world.getBlockState(pos);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            if (canSilkHarvest(world, pos, state, player) && EnchantmentHelper.getSilkTouchModifier(player)) {
                List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, state, 0);
                for (ItemStack stack : drops) {
                    spawnAsEntity(world, pos, stack);
                }
            } else
                dropBlockAsItem(world, pos, state, 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getDrops(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    public List<ItemStack> getBlockDroppedSilkTouch(World world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getBlockDroppedSilkTouch(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).canSilkHarvest(player);
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, pos, world.getBlockState(pos), 0);
        return drops.get(0);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).isPoweringTo(side) ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).canConnectRedstone(side);
        return false;
    }

    public void initFromItem(World world, BlockPos pos, ItemStack stack) {
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
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        try {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileMachineBase)
                ((TileMachineBase) tile).onNeighborBlockChange(state, neighborBlock);
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()", 10, error);
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

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
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

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (M type : proxy.getCreativeList()) {
            if (type.isAvailable())
                list.add(type.getItem());
        }
    }

    @Override
    public final boolean isOpaqueCube() {
        return fullBlock;
    }

    @Override
    public int getLightOpacity(IBlockAccess world, BlockPos pos) {
        int meta = getMetaFromState(world.getBlockState(pos));
        return metaOpacity[meta];
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getResistance(exploder) * 3f / 5f;
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).canCreatureSpawn(type);
        return super.canCreatureSpawn(world, pos, type);
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getHardness();
        return super.getBlockHardness(worldIn, pos);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return proxy instanceof IComparatorOverride;
    }

    /**
     * Value is provided by the tile entity
     */
    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IComparatorValueProvider)
            return ((IComparatorValueProvider) tile).getComparatorInputOverride(worldIn, pos, null);
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
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return BoundingBoxManager.getCollisionBox(worldIn, pos, getMachineType(worldIn.getBlockState(pos)));
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return BoundingBoxManager.getSelectionBox(worldIn, pos, getMachineType(worldIn.getBlockState(pos)));
    }
}
