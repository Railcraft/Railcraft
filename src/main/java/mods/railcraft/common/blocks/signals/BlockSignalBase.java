/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;

public abstract class BlockSignalBase extends BlockContainer implements IPostConnection {
    private final int renderType;

    public BlockSignalBase(int renderType) {
        super(new MaterialStructure());
        this.renderType = renderType;
        setStepSound(Block.soundTypeMetal);
        setResistance(50);
        setCreativeTab(CreativeTabs.tabTransport);
//        setHarvestLevel("pickaxe", 2);
        setHarvestLevel("crowbar", 0);
    }

    public abstract ISignalTileDefinition getSignalType(int meta);

    @Override
    public abstract IIcon getIcon(int side, int meta);

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float u1, float u2, float u3) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null)
            if (current.getItem() instanceof IActivationBlockingItem)
                return false;
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).blockActivated(side, player);
        return false;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).rotateBlock(axis);
        return false;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).getValidRotations();
        return super.getValidRotations(world, x, y, z);
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        super.onPostBlockPlaced(world, x, y, z, meta);
        if (SignalTools.printSignalDebug) {
            Game.logTrace(Level.INFO, 10, "Signal Block onPostBlockPlaced. [{0}, {1}, {2}]", x, y, y);
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            ((TileSignalFoundation) tile).onBlockPlaced();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        if (SignalTools.printSignalDebug) {
            Game.logTrace(Level.INFO, 10, "Signal Block onBlockPlacedBy. [{0}, {1}, {2}]", x, y, z);
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            ((TileSignalFoundation) tile).onBlockPlacedBy(entityliving, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        try {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileSignalFoundation) {
                TileSignalFoundation structure = (TileSignalFoundation) tile;
                if (structure.getSignalType().needsSupport() && !world.isSideSolid(x, y - 1, z, ForgeDirection.UP))
                    world.func_147480_a(x, y, z, true);
                else
                    structure.onNeighborBlockChange(block);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Error in BlockSignalBase.onNeighborBlockChange()", 10, error);
            throw error;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (SignalTools.printSignalDebug) {
            Game.logTrace(Level.INFO, 10, "Signal Block breakBlock. [{0}, {1}, {2}]", x, y, z);
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            ((TileSignalFoundation) tile).onBlockRemoval();
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            ((TileSignalFoundation) tile).setBlockBoundsBasedOnState(world, i, j, k);
        else
            setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).getCollisionBoundingBoxFromPool(world, i, j, k);
        setBlockBounds(0, 0, 0, 1, 1, 1);
        return super.getCollisionBoundingBoxFromPool(world, i, j, k);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).getSelectedBoundingBoxFromPool(world, x, y, z);
        return AxisAlignedBB.getBoundingBox((double) x + minX, (double) y + minY, (double) z + minZ, (double) x + maxX, (double) y + maxY, (double) z + maxZ);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        if (y < 0)
            return 0;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ISignalTile)
            return ((ISignalTile) tile).getLightValue();
        return 0;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).getHardness();
        return 3;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).isSideSolid(world, i, j, k, side);
        return false;
    }

    //    @Override
//    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
//        int md = world.getBlockMetadata(x, y, z);
//        EnumSignal type = EnumSignal.fromId(md);
//        return super.canPlaceBlockOnSide(world, x, y, z, side) && (!type.needsSupport() || world.isSideSolid(x, y - 1, z, ForgeDirection.UP));
//    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public abstract TileEntity createTileEntity(World world, int metadata);

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).canConnectRedstone(dir);
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation)
            return ((TileSignalFoundation) tile).getPowerOutput(side);
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity t = world.getTileEntity(x, y, z);
        if (t instanceof ISignalTile)
            return ConnectStyle.TWO_THIN;
        return ConnectStyle.NONE;
    }
}
