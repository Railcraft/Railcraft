/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockCube extends Block {

    private static BlockCube instance;
    @SideOnly(Side.CLIENT)
    private RenderInfo override;

    public BlockCube() {
        super(Material.rock);
        setRegistryName("railcraft.cube");
        setResistance(20);
        setHardness(5);
        setStepSound(RailcraftSound.getInstance());

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        setHarvestLevel("pickaxe", 1, EnumCube.COKE_BLOCK.ordinal());
        setHarvestLevel("pickaxe", 1, EnumCube.ABYSSAL_STONE.ordinal());
        setHarvestLevel("pickaxe", 2, EnumCube.STEEL_BLOCK.ordinal());
        setHarvestLevel("pickaxe", 1, EnumCube.CONCRETE_BLOCK.ordinal());
        setHarvestLevel("crowbar", 0, EnumCube.CONCRETE_BLOCK.ordinal());
        setHarvestLevel("axe", 0, EnumCube.CREOSOTE_BLOCK.ordinal());
        setHarvestLevel("crowbar", 0, EnumCube.CREOSOTE_BLOCK.ordinal());
        setHarvestLevel("shovel", 3, EnumCube.CRUSHED_OBSIDIAN.ordinal());
    }

    public static BlockCube getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("cube")) {
                instance = new BlockCube();
                RailcraftRegistry.register(instance, ItemCube.class);

                EntityTunnelBore.addMineableBlock(instance);

                ForestryPlugin.addBackpackItem("miner", EnumCube.COKE_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("miner", EnumCube.COPPER_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("miner", EnumCube.LEAD_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("miner", EnumCube.STEEL_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("miner", EnumCube.TIN_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("builder", EnumCube.CONCRETE_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("builder", EnumCube.CREOSOTE_BLOCK.getItem());
                ForestryPlugin.addBackpackItem("digger", EnumCube.ABYSSAL_STONE.getItem());
                ForestryPlugin.addBackpackItem("digger", EnumCube.QUARRIED_STONE.getItem());

                MicroBlockPlugin.addMicroBlockCandidate(instance, EnumCube.CONCRETE_BLOCK.ordinal());
                MicroBlockPlugin.addMicroBlockCandidate(instance, EnumCube.CREOSOTE_BLOCK.ordinal());
                MicroBlockPlugin.addMicroBlockCandidate(instance, EnumCube.STEEL_BLOCK.ordinal());
                MicroBlockPlugin.addMicroBlockCandidate(instance, EnumCube.ABYSSAL_STONE.ordinal());
                MicroBlockPlugin.addMicroBlockCandidate(instance, EnumCube.QUARRIED_STONE.ordinal());
            }
    }

    public static String getBlockNameFromMetadata(int meta) {
        return EnumCube.fromOrdinal(meta).getTag();
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        int meta = worldIn.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(meta).getHardness();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void setTextureOverride(RenderInfo info) {
        override = info;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        int meta = world.getBlockMetadata(x, y, z);
        EnumCube.fromOrdinal(meta).getBlockDef().onNeighborBlockChange(world, pos, neighbor);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        EnumCube.fromOrdinal(meta).getBlockDef().updateTick(worldIn, pos, rand);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumCube.fromOrdinal(meta).getBlockDef().onBlockPlaced(worldIn, pos);
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int meta = worldIn.getBlockMetadata(pos);

        EnumCube.fromOrdinal(meta).getBlockDef().randomDisplayTick(worldIn, pos, rand);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        int meta = worldIn.getBlockMetadata(pos);

        EnumCube.fromOrdinal(meta).getBlockDef().onBlockAdded(worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        EnumCube.fromOrdinal(meta).getBlockDef().onBlockRemoval(worldIn, pos);
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        int meta = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(meta).getBlockDef().removedByPlayer(world, player, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        if (override != null) return true;
        return super.shouldSideBeRendered(worldIn, pos, side);
    }

    @Override
    public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        int meta = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(meta).getBlockDef().canCreatureSpawn(type, world, pos);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumCube type : EnumCube.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(meta).getResistance() * 3f / 5f;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        int metadata = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(metadata).getBlockDef().getFireSpreadSpeed(world, pos, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        int metadata = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(metadata).getBlockDef().getFlammability(world, pos, face);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        int metadata = world.getBlockMetadata(pos);
        return EnumCube.fromOrdinal(metadata).getBlockDef().isFlammable(world, pos, face);
    }

}
