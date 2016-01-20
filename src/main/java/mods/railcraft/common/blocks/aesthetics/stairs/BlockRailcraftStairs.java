/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.BlockStairs;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BlockRailcraftStairs extends BlockStairs implements IBlockSoundProvider {

    static BlockRailcraftStairs block;

    public static BlockRailcraftStairs getBlock() {
        return block;
    }

    public static ItemStack getItem(EnumBlockMaterial mat) {
        if (block == null) return null;
        return new ItemStack(block, 1, mat.ordinal());
    }

    public static ItemStack getItem(EnumBlockMaterial mat, int qty) {
        if (block == null) return null;
        return new ItemStack(block, qty, mat.ordinal());
    }

    public static String getTag(EnumBlockMaterial mat) {
        return "railcraft.stair." + mat.name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    public static boolean isEnabled(EnumBlockMaterial mat) {
        return ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag(mat)) && getBlock() != null;
    }

    private final int renderId;
    public static int currentRenderPass;

    protected BlockRailcraftStairs(int renderId) {
        super(Blocks.stonebrick, 0);
        this.renderId = renderId;
        this.setStepSound(RailcraftSound.getInstance());
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
        isBlockContainer = true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return new ItemStack(this, 1, ((TileStair) tile).getStair().ordinal());
        return null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumBlockMaterial mat : EnumBlockMaterial.creativeList) {
            if (isEnabled(mat))
                list.add(getItem(mat));
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileStair)
            items.add(new ItemStack(this, 1, ((TileStair) tile).getStair().ordinal()));
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
            ((TileStair) tile).setStair(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, state, 0);
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
            return ((TileStair) tile).getStair().getBlockHardness(worldIn, pos);
        return super.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getStair().getExplosionResistance(exploder);
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, block, target, effectRenderer, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(world, block, pos, meta, effectRenderer, null);
    }

    @Override
    public SoundType getSound(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getStair().getSound();
        return null;
    }
}
