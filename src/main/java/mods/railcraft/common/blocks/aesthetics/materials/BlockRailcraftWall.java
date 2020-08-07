/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostBase;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class BlockRailcraftWall extends BlockWall implements IMaterialBlock {

    public static int currentRenderPass;

    public BlockRailcraftWall() {
        super(Blocks.STONEBRICK);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return Materials.class;
    }

    //TODO: recipe??
    @Override
    public void finalizeDefinition() {
        IMaterialBlock.super.finalizeDefinition();
        List<Materials> mats = Materials.getValidMats();
        for (Materials mat : mats) {
            ItemStack stack = getStack(mat);
            if (InvTools.nonEmpty(stack)) {
                RailcraftRegistry.register(this, mat, stack);
                if (!Materials.MAT_SET_FROZEN.contains(mat))
                    ForestryPlugin.addBackpackItem("forestry.builder", stack);
            }
        }
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return Materials.getStack(this, qty, variant);
    }

    @Override
    public String getTranslationKey(Materials mat) {
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
        else return block instanceof BlockPostBase;
    }

    private boolean canConnectToOld(IBlockAccess worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block != Blocks.BARRIER && (block == this || block instanceof BlockFenceGate || ((iblockstate.getMaterial().isOpaque() && iblockstate.isFullCube()) && iblockstate.getMaterial() != Material.GOURD));
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
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.addAll(Materials.getCreativeList().stream().map(this::getStack).filter(InvTools::nonEmpty).collect(Collectors.toList()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return MatTools.getDrops(world, pos, state, fortune);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        MatTools.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
        return world.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return MatTools.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return MatTools.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return MatTools.getSound(world, pos);
    }
}
