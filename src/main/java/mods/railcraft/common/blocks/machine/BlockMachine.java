/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mods.railcraft.api.core.IPostConnection;
import org.apache.logging.log4j.Level;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.beta.MachineProxyBeta;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

public class BlockMachine extends BlockContainer implements IPostConnection {

    private final IMachineProxy proxy;
    private final int renderId;
    private final int[] metaOpacity;

    public BlockMachine(int renderId, IMachineProxy proxy, boolean opaque, int[] metaOpacity) {
        super(Material.rock);
        setResistance(4.5F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setTickRandomly(true);
        this.proxy = proxy;
        this.opaque = opaque;
        this.renderId = renderId;
        this.metaOpacity = metaOpacity;

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        lightOpacity = opaque ? 255 : 0;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    /**
     * Returns the default ambient occlusion value based on block opacity
     * @return
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue() {
        return 1;
    }

    public IMachineProxy getMachineProxy() {
        return proxy;
    }

    public IEnumMachine getMachineType(World world, int x, int y, int z) {
        int meta = WorldPlugin.getBlockMetadata(world, x, y, z);
        return proxy.getMachine(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        proxy.registerIcons(iconRegister);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getIcon(side);
        int meta = world.getBlockMetadata(i, j, k);
        return getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return proxy.getMachine(meta).getTexture(side);
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).colorMultiplier();
        return super.colorMultiplier(world, x, y, z);
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).recolourBlock(colour);
        return false;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float u1, float u2, float u3) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).blockActivated(player, side);
        return false;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).rotateBlock(axis);
        return false;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getValidRotations();
        return super.getValidRotations(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).randomDisplayTick(random);
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).isSideSolid(side);
        return true;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            if (canSilkHarvest(world, player, x, y, z, 0) && EnchantmentHelper.getSilkTouchModifier(player)) {
                List<ItemStack> drops = getBlockDroppedSilkTouch(world, x, y, z, 0, 0);
                for (ItemStack stack : drops) {
                    dropBlockAsItem(world, x, y, z, stack);
                }
            } else
                dropBlockAsItem(world, x, y, z, 0, 0);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getDrops(fortune);
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    public ArrayList<ItemStack> getBlockDroppedSilkTouch(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getBlockDroppedSilkTouch(fortune);
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).canSilkHarvest(player);
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        List<ItemStack> drops = getBlockDroppedSilkTouch(world, x, y, z, 0, 0);
        return drops.get(0);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).isPoweringTo(side) ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).canConnectRedstone(dir);
        return false;
    }

    public void initFromItem(World world, int i, int j, int k, ItemStack stack) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).initFromItem(stack);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockPlacedBy(entityliving, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
        try {
            TileEntity tile = world.getTileEntity(i, j, k);
            if (tile instanceof TileMachineBase)
                ((TileMachineBase) tile).onNeighborBlockChange(block);
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()", 10, error);
            if (Game.IS_DEBUG)
                throw error;
        }
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockAdded();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            ((TileMachineBase) tile).onBlockRemoval();
//        world.notifyBlocksOfNeighborChange(x + 1, y, z, blockID);
//        world.notifyBlocksOfNeighborChange(x - 1, y, z, blockID);
//        world.notifyBlocksOfNeighborChange(x, y, z + 1, blockID);
//        world.notifyBlocksOfNeighborChange(x, y, z - 1, blockID);
//        world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
//        world.notifyBlocksOfNeighborChange(x, y + 1, z, blockID);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return proxy.getMachine(meta).getTileEntity();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        if (y < 0)
            return 0;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getLightValue();
        return 0;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (IEnumMachine type : proxy.getCreativeList()) {
            if (type.isAvaliable())
                list.add(type.getItem());
        }
    }

    @Override
    public final boolean isOpaqueCube() {
        return opaque;
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return metaOpacity[meta];
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double srcX, double srcY, double srcZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getResistance(exploder) * 3f / 5f;
        return super.getExplosionResistance(exploder, world, x, y, z, minX, minY, minZ);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).canCreatureSpawn(type);
        return super.canCreatureSpawn(type, world, x, y, z);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).getHardness();
        return super.getBlockHardness(world, x, y, z);
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        if (proxy instanceof IComparatorOverride)
        	return true;
        return false;
    }

    /**
     * Value is provided by the tile entity
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IComparatorValueProvider)
            return ((IComparatorValueProvider) tile).getComparatorInputOverride(world, x, y, z, side);
        return 0;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMachineBase)
            return ((TileMachineBase) tile).connectsToPost(side);
        return ConnectStyle.NONE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return BoundingBoxManager.getCollisionBox(world, x, y, z, getMachineType(world, x, y, z));
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return BoundingBoxManager.getSelectionBox(world, x, y, z, getMachineType(world, x, y, z));
    }

}
