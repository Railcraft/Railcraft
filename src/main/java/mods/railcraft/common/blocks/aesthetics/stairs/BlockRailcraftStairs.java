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
import mods.railcraft.common.util.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
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
    private final int renderId;

    BlockRailcraftStairs(int renderId) {
        super(Blocks.STONEBRICK.getDefaultState());
        this.renderId = renderId;
        this.setStepSound(RailcraftSound.instance());
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

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, SHAPE, MATERIAL);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState actState = super.getActualState(state, worldIn, pos);
        return actState.withProperty(MATERIAL, getMat(worldIn, pos));
    }

    @Override
    public ItemStack getPickBlock(RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair) {
            return getItem(((TileStair) tile).getMaterial());
        }
        return null;
    }

    public BlockMaterial getMat(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair) {
            return ((TileStair) tile).getMaterial();
        }
        return SANDY_BRICK;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockMaterial mat : BlockMaterial.CREATIVE_LIST) {
            if (isEnabled(mat) && STAIR_MATS.contains(mat))
                list.add(getItem(mat));
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileStair)
            items.add(getItem(((TileStair) tile).getMaterial()));
        return items;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
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
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            IBlockState state = WorldPlugin.getBlockState(world, pos);
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
        return new TileStair();
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getBlockHardness(worldIn, pos);
        return super.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getExplosionResistance(exploder);
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, RayTraceResult target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, block, target, effectRenderer, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, block, pos, state, effectRenderer, null);
    }

    @Override
    public SoundType getSound(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getMaterial().getSound();
        return null;
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        IBlockState matState = state.getValue(MATERIAL).getState();
        if (matState != null && matState.getBlock() != null)
            return matState.getBlock().getHarvestTool(matState);
        return "pickaxe";
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return super.isToolEffective(type, state);
    }
}
