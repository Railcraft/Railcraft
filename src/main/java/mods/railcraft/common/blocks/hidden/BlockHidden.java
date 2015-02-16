/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.hidden;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockHidden extends BlockContainer {

    private static BlockHidden block;

    public static BlockHidden getBlock() {
        return block;
    }

    public static void registerBlock() {
        if (block == null && RailcraftConfig.isBlockEnabled("residual.heat")) {
            block = new BlockHidden();
            RailcraftRegistry.register(block);
        }
    }

    public BlockHidden() {
        super(Material.air);
        setBlockName("railcraft.residual.heat");
        disableStats();
        setStepSound(new SoundType("null", 0, 0));

        GameRegistry.registerTileEntity(TileHidden.class, "RCHiddenTile");
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        try {
            return Blocks.tallgrass.getIcon(side, meta);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
        return null;
    }

    @Override
    public final void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileHidden();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }

    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return true;
    }

}
