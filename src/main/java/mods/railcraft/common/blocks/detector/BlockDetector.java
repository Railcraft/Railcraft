/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.blocks.RailcraftBlockContainer;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

public class BlockDetector extends RailcraftBlockContainer {

    @SuppressWarnings("WeakerAccess")
    public BlockDetector() {
        super(Material.ROCK);
        setResistance(4.5F);
        setHardness(2.0F);
        setSoundType(SoundType.STONE);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        GameRegistry.registerTileEntity(TileDetector.class, "RCDetectorTile");
    }

    @Override
    public void initializeDefinintion() {
        //            HarvestPlugin.setStateHarvestLevel(block, "pickaxe", 2);
        HarvestPlugin.setBlockHarvestLevel("crowbar", 0, this);

        for (EnumDetector d : EnumDetector.VALUES) {
            ItemStack stack = new ItemStack(this, 1, d.ordinal());
            RailcraftRegistry.register(stack);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.ITEM.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "plankWood",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.ANY.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "stone",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.EMPTY.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONEBRICK, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.MOB.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONEBRICK, 1, 1),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.MOB.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Blocks.MOSSY_COBBLESTONE,
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.POWERED.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "cobblestone",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.PLAYER.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONE_SLAB, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.EXPLOSIVE.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "slabWood",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.ANIMAL.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.LOG, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.AGE.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.LOG, 1, 1),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.ADVANCED.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "ingotSteel",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.TANK.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "ingotBrick",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.SHEEP.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Blocks.WOOL,
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.VILLAGER.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Items.LEATHER,
                'P', Blocks.STONE_PRESSURE_PLATE);
    }

    @Override
    public void finalizeDefinition() {
        if (BrickTheme.INFERNAL.getBlock() != null)
            CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.LOCOMOTIVE.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', BrickTheme.INFERNAL.get(BrickVariant.BRICK, 1),
                    'P', Blocks.STONE_PRESSURE_PLATE);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            return detector.getDetector().getType().getItem();
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileDetector)
            items.add(((TileDetector) tile).getDetector().getType().getItem());
        return items;
    }

    //TODO: Move drop code here? We have a reference to the TileEntity now.
    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector)
            ((TileDetector) tile).getDetector().onBlockRemoved();
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return world.setBlockToAir(pos);
    }

    @Nonnull
    @Override
    public TileEntity createNewTileEntity(@Nonnull World var1, int meta) {
        return new TileDetector();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            ((TileDetector) tile).direction = MiscTools.getSideFacingPlayer(pos, placer);
            ((TileDetector) tile).onBlockPlacedBy(state, placer, stack);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking())
            return false;
        if (heldItem != null) {
            Item item = heldItem.getItem();
            if (item instanceof IActivationBlockingItem)
                return false;
            else if (TrackTools.isRailItem(item))
                return false;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof TileDetector && ((TileDetector) tile).blockActivated(playerIn);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            detector.onNeighborBlockChange(blockIn);
        }
    }

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            if (detector.direction == axis)
                detector.direction = axis.getOpposite();
            else
                detector.direction = axis;
            markBlockForUpdate(world, pos);
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos) {
        return EnumFacing.VALUES;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDetector)
            return ((TileDetector) tile).getDetector().getHardness();
        return super.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
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
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
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
    public int getStrongPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return getWeakPower(state, worldIn, pos, side);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        markBlockForUpdate(state, worldIn, pos);
        if (Game.isClient(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock());
        }
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (Game.isClient(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock());
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
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
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumDetector detector : EnumDetector.VALUES) {
            if (detector.isEnabled())
                list.add(detector.getItem());
        }
    }
}
