/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

public class BlockDetector extends BlockContainer {

    private static BlockDetector block;

    public BlockDetector() {
        super(Material.rock);

        setUnlocalizedName("railcraft.detector");
        setResistance(4.5F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        GameRegistry.registerTileEntity(TileDetector.class, "RCDetectorTile");
    }

    public static void registerBlock() {
        if (block == null && RailcraftConfig.isBlockEnabled("detector")) {
            block = new BlockDetector();
            RailcraftRegistry.register(block, ItemDetector.class);

//            HarvestPlugin.setStateHarvestLevel(block, "pickaxe", 2);
            HarvestPlugin.setBlockHarvestLevel("crowbar", 0, block);

            for (EnumDetector d : EnumDetector.VALUES) {
                ItemStack stack = new ItemStack(block, 1, d.ordinal());
                RailcraftRegistry.register(stack);
            }
        }
    }

    public static BlockDetector getBlock() {
        return block;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            return detector.getDetector().getType().getItem();
        }
        return super.getPickBlock(target, world, pos, player);
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileDetector)
            items.add(((TileDetector) tile).getDetector().getType().getItem());
        return items;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector)
            ((TileDetector) tile).getDetector().onBlockRemoved();
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, state, 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDetector();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            ((TileDetector) tile).direction = MiscTools.getSideFacingPlayer(pos, placer);
            ((TileDetector) tile).onBlockPlacedBy(placer, stack);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking())
            return false;
        ItemStack current = playerIn.getCurrentEquippedItem();
        if (current != null) {
            Item item = current.getItem();
            if (item instanceof IActivationBlockingItem)
                return false;
            else if (TrackTools.isRailItem(item))
                return false;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector)
            return ((TileDetector) tile).blockActivated(playerIn);
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            detector.onNeighborBlockChange(neighborBlock);
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            if (detector.direction == axis)
                detector.direction = axis.getOpposite();
            else
                detector.direction = axis;
            world.markBlockForUpdate(pos);
            return true;
        }
        return false;
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return EnumFacing.VALUES;
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector)
            return ((TileDetector) tile).getDetector().getHardness();
        return super.getBlockHardness(worldIn, pos);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the
     * specified side. If isBlockNormalCube returns true, standard redstone
     * propagation rules will apply instead and this will not be called. Args:
     * World, X, Y, Z, side. Note that the side is reversed - eg it is 1 (up)
     * when checking the bottom of the block.
     */
    @Override
    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity t = worldIn.getTileEntity(pos);
        if (t instanceof TileDetector) {
            TileDetector tile = (TileDetector) t;
            if (tile.direction == side.getOpposite())
                return tile.powerState;
        }
        return PowerPlugin.NO_POWER;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the
     * specified side. Args: World, X, Y, Z, side. Note that the side is
     * reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return getWeakPower(worldIn, pos, state, side);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        worldIn.markBlockForUpdate(pos);
        if (Game.isNotHost(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock());
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (Game.isNotHost(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock());
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileDetector) {
            TileDetector tile = (TileDetector) t;
            if (side == UP && tile.direction == EAST)
                return true;
            if (side == SOUTH && tile.direction == WEST)
                return true;
            if (side == NORTH && tile.direction == SOUTH)
                return true;
            if (side == DOWN && tile.direction == NORTH)
                return true;
        }
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumDetector detector : EnumDetector.VALUES) {
            if (detector.isEnabled())
                list.add(detector.getItem());
        }
    }
}
