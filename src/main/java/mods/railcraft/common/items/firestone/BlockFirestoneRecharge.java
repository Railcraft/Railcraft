/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockFirestoneRecharge extends BlockContainer {
    public static final PropertyBool CRACKED = PropertyBool.create("cracked");
    private static Block block;

    public static Block getBlock() {
        return block;
    }

    public static void registerBlock() {
        if (block == null) {
            String tag = "railcraft.firestone.recharge";
            if (RailcraftConfig.isBlockEnabled(tag)) {
                block = new BlockFirestoneRecharge().setUnlocalizedName(tag);
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
        setDefaultState(blockState.getBaseState().withProperty(CRACKED, false));

        GameRegistry.registerTileEntity(TileFirestoneRecharge.class, "RCFirestoneRechargeTile");
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CRACKED, meta != 0);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CRACKED) ? 1 : 0;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, CRACKED);
    }

//    @Override
//    public IIcon getIcon(int side, int meta) {
//        return Blocks.obsidian.getIcon(side, meta);
//    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return ItemFirestoneRefined.getItemCharged();
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFirestoneRecharge) {
            TileFirestoneRecharge firestone = (TileFirestoneRecharge) tile;
            Item item = state.getValue(CRACKED) ? ItemFirestoneRefined.item : ItemFirestoneCracked.item;
            ItemStack drop = new ItemStack(item, 1, ItemFirestoneRefined.item.getMaxDamage() - firestone.charge);
            if (firestone.getItemName() != null)
                drop.setStackDisplayName(firestone.getItemName());
            drops.add(drop);
        } else
            drops.add(ItemFirestoneRefined.getItemEmpty());
        return drops;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world))
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return WorldPlugin.setBlockToAir(world, pos);
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
    public boolean isFullCube() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
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
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        return true;
    }

    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return true;
    }

}
