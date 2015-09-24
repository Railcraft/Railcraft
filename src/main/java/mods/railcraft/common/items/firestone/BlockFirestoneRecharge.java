/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockFirestoneRecharge extends BlockContainer {

    private static Block block;

    public static Block getBlock() {
        return block;
    }

    public static void registerBlock() {
        if (block == null) {
            String tag = "railcraft.firestone.recharge";
            if (RailcraftConfig.isBlockEnabled(tag)) {
                block = new BlockFirestoneRecharge().setBlockName(tag);
                RailcraftRegistry.register(block);
            }
        }
    }

    public BlockFirestoneRecharge() {
        super(Material.rock);
        disableStats();
        setStepSound(new SoundType("null", 0, 0));
        float f = 0.2F;
        setBlockBounds(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 0.9f, 0.5F + f);
        setLightLevel(1);

        GameRegistry.registerTileEntity(TileFirestoneRecharge.class, "RCFirestoneRechargeTile");
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.obsidian.getIcon(side, meta);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return ItemFirestoneRefined.getItemCharged();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileFirestoneRecharge) {
            TileFirestoneRecharge firestone = (TileFirestoneRecharge) tile;
            Item item = meta == 0 ? ItemFirestoneRefined.item : ItemFirestoneCracked.item;
            ItemStack drop = new ItemStack(item, 1, ItemFirestoneRefined.item.getMaxDamage() - firestone.charge);
            if (firestone.getItemName() != null)
                drop.setStackDisplayName(firestone.getItemName());
            drops.add(drop);
        } else
            drops.add(ItemFirestoneRefined.getItemEmpty());
        return drops;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world))
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        return world.setBlockToAir(x, y, z);
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int s) {
        return null;
    }

//    @Override
//    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
//    }
//
//    @Override
//    public boolean canCollideCheck(int par1, boolean par2) {
//        return false;
//    }
//    @Override
//    public boolean isCollidable() {
//        return false;
//    }
//
//    @Override
//    public int getMobilityFlag() {
//        return 1;
//    }
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFirestoneRecharge();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
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
