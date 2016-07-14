/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.common.blocks.aesthetics.post.BlockPostBase;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class BlockRailcraftWall extends BlockWall implements IMaterialBlock {

    public static int currentRenderPass;

    public BlockRailcraftWall() {
        super(Blocks.STONEBRICK);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void finalizeDefinition() {
        for (Materials mat : Materials.getValidMats()) {
            ItemStack stack = getStack(mat);
            if (stack != null) {
                RailcraftRegistry.register(stack);
                if (!Materials.MAT_SET_FROZEN.contains(mat))
                    ForestryPlugin.addBackpackItem("forestry.builder", stack);
            }
        }
    }

    @Override
    public String getUnlocalizedName(Materials mat) {
        return "tile.railcraft.wall." + mat.getLocalizationSuffix();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{UP, NORTH, EAST, WEST, SOUTH, VARIANT}, new IUnlistedProperty[]{Materials.MATERIAL_PROPERTY});
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean north = canConnectTo(worldIn, pos.north());
        boolean east = canConnectTo(worldIn, pos.east());
        boolean south = canConnectTo(worldIn, pos.south());
        boolean west = canConnectTo(worldIn, pos.west());
        boolean smooth = north && !east && south && !west || !north && east && !south && west;
        state = state.withProperty(UP, !smooth || !worldIn.isAirBlock(pos.up())).withProperty(NORTH, north).withProperty(EAST, east).withProperty(SOUTH, south).withProperty(WEST, west);


        IExtendedBlockState actState = (IExtendedBlockState) state;
        actState = actState.withProperty(Materials.MATERIAL_PROPERTY, MatTools.getMat(worldIn, pos));
        return actState;
    }

    /**
     * Return whether an adjacent block can connect to a wall.
     */
    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
        if (canConnectToOld(worldIn, pos))
            return true;

        Block block = WorldPlugin.getBlock(worldIn, pos);

        if (block instanceof BlockRailcraftWall)
            return true;
        else if (block instanceof BlockPostBase)
            return true;
        return false;
    }

    private boolean canConnectToOld(IBlockAccess worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block == Blocks.BARRIER ? false : (block != this && !(block instanceof BlockFenceGate) ? (iblockstate.getMaterial().isOpaque() && iblockstate.isFullCube() ? iblockstate.getMaterial() != Material.GOURD : false) : true);
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as
     * fences.
     *
     * @param world The current world
     * @return True to allow the torch to be placed
     */
    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.addAll(Materials.getCreativeList().stream().map(this::getStack).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        return MatTools.getDrops(world, pos, state, fortune);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        MatTools.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
        return world.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileMaterial();
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return MatTools.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nonnull Entity exploder, Explosion explosion) {
        return MatTools.getExplosionResistance(world, pos, exploder, explosion);
    }
}
