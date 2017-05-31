/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public abstract class BlockWayObject extends BlockContainerRailcraft implements IPostConnection {

    protected BlockWayObject() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setResistance(50);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
//        setStateHarvestLevel("pickaxe", 2);
        setHarvestLevel("crowbar", 0);
    }

    public abstract IWayObjectDefinition getSignalType(IBlockState state);

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!InvTools.isEmpty(heldItem))
            if (heldItem.getItem() instanceof IActivationBlockingItem)
                return false;
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof TileWayObject && ((TileWayObject) tile).blockActivated(side, playerIn, hand, heldItem);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileWayObject && ((TileWayObject) tile).rotateBlock(axis);
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getValidRotations();
        return super.getValidRotations(world, pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (SignalTools.printSignalDebug) {
            Game.logTrace(Level.INFO, 10, "Signal Block onBlockPlacedBy. [{0}]", pos);
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            ((TileWayObject) tile).onBlockPlacedBy(state, placer, stack);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
        try {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileWayObject) {
                TileWayObject structure = (TileWayObject) tile;
                if (structure.getSignalType().needsSupport() && !worldIn.isSideSolid(pos.down(), EnumFacing.UP))
                    worldIn.destroyBlock(pos, true);
                else
                    structure.onNeighborBlockChange(state, neighborBlock);
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, 10, error, "Error in BlockSignalBase.onNeighborBlockChange()");
            throw error;
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (SignalTools.printSignalDebug) {
            Game.logTrace(Level.INFO, 10, "Signal Block breakBlock. [{0}]", pos);
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            ((TileWayObject) tile).onBlockRemoval();
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getBoundingBox(source, pos);
        return AABBFactory.FULL_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getBoundingBox(worldIn, pos);
        return AABBFactory.FULL_BOX;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getSelectedBoundingBox(worldIn, pos);
        return AABBFactory.FULL_BOX;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (pos.getY() < 0)
            return 0;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSignalBase)
            return ((TileSignalBase) tile).getLightValue();
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getHardness();
        return 3;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileWayObject && ((TileWayObject) tile).isSideSolid(world, pos, side);
    }

    //    @Override
//    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
//        int md = world.getBlockMetadata(x, y, z);
//        EnumSignal type = EnumSignal.fromId(md);
//        return super.canPlaceBlockOnSide(world, x, y, z, side) && (!type.needsSupport() || world.isSideSolid(x, y - 1, z, EnumFacing.UP));
//    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileWayObject && ((TileWayObject) tile).canConnectRedstone(side);
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileWayObject)
            return ((TileWayObject) tile).getPowerOutput(side);
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity t = WorldPlugin.getBlockTile(world, pos);
        if (t instanceof TileSignalBase)
            return ConnectStyle.TWO_THIN;
        return ConnectStyle.NONE;
    }
}
