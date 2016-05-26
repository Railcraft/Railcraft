/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import mods.railcraft.common.util.sounds.RailcraftSound;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static mods.railcraft.common.blocks.aesthetics.BlockMaterial.SANDY_BRICK;
import static mods.railcraft.common.blocks.aesthetics.BlockMaterial.STAIR_MATS;

public class BlockRailcraftStairs extends BlockStairs implements IBlockSoundProvider {
    public static final PropertyEnum<BlockMaterial> MATERIAL = PropertyEnum.create("material", BlockMaterial.class, EnumSet.complementOf(BlockMaterial.VANILLA_STAIRS));
    public static int currentRenderPass;
    static BlockRailcraftStairs block;

    BlockRailcraftStairs() {
        super(Blocks.STONEBRICK.getDefaultState());
        setSoundType(RailcraftSound.instance());
        setDefaultState(getDefaultState().withProperty(MATERIAL, BlockMaterial.SANDY_BRICK));
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
        isBlockContainer = true;
    }

    public static BlockRailcraftStairs getBlock() {
        return block;
    }

    public static ItemStack getItem(BlockMaterial mat) {
        return getItem(mat, 1);
    }

    public static ItemStack getItem(BlockMaterial mat, int qty) {
        if (block == null) return null;
        ItemStack stack = new ItemStack(block, qty);
        MaterialRegistry.tagItemStack(stack, ItemStair.MATERIAL_KEY, mat);
        return stack;
    }

    public static String getTag(BlockMaterial mat) {
        return "tile.railcraft.stair." + mat.getLocalizationSuffix();
    }

    public static boolean isEnabled(BlockMaterial mat) {
        return RailcraftModuleManager.isModuleEnabled(ModuleStructures.class) && RailcraftConfig.isSubBlockEnabled(getTag(mat)) && getBlock() != null;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, SHAPE, MATERIAL);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState actState = super.getActualState(state, worldIn, pos);
        return actState.withProperty(MATERIAL, getMat(worldIn, pos));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair) {
            return getItem(((TileStair) tile).getMaterial());
        }
        return getItem(BlockMaterial.SANDY_BRICK);
    }

    public BlockMaterial getMat(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair) {
            return ((TileStair) tile).getMaterial();
        }
        return SANDY_BRICK;
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockMaterial mat : BlockMaterial.CREATIVE_LIST) {
            if (isEnabled(mat) && STAIR_MATS.contains(mat))
                list.add(getItem(mat));
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileStair)
            items.add(getItem(((TileStair) tile).getMaterial()));
        return items;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, @Nonnull Random random) {
        return 1;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileStair)
            ((TileStair) tile).setStair(ItemStair.getMat(stack));
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
        return new TileStair();
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getBlockHardness(worldIn, pos);
        return super.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nonnull Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getExplosionResistance(exploder);
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager particleManager) {
        return ParticleHelper.addHitEffects(worldObj, block, target, particleManager, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, block, pos, state, particleManager, null);
    }

    @Override
    public SoundType getSound(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getSound();
        return null;
    }

    @Nonnull
    @Override
    public String getHarvestTool(@Nonnull IBlockState state) {
        IBlockState matState = state.getValue(MATERIAL).getState();
        if (matState != null)
            return matState.getBlock().getHarvestTool(matState);
        return "pickaxe";
    }
}
