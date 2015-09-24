/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.stairs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import net.minecraft.block.*;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
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
    public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int md, int fortune) {
        TileEntity tile = world.getTileEntity(i, j, k);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileStair)
            items.add(new ItemStack(this, 1, ((TileStair) tile).getStair().ordinal()));
        return items;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair)
            ((TileStair) tile).setStair(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, x, y, z, 0, 0);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        super.breakBlock(world, x, y, z, block, par6);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileStair();
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getStair().getBlockHardness(world, x, y, z);
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getStair().getExplosionResistance(entity);
        return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        currentRenderPass = pass;
        return pass == 0 || pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return EnumBlockMaterial.fromOrdinal(meta).getIcon(side);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getTexture(side);
        return super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, block, target, effectRenderer, null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World worldObj, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return ParticleHelper.addDestroyEffects(worldObj, block, x, y, z, meta, effectRenderer, null);
    }

    @Override
    public SoundType getSound(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair)
            return ((TileStair) tile).getStair().getSound();
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
    }

}
