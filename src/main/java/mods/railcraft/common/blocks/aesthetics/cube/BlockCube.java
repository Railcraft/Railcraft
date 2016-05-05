/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockCube extends Block {

    private static BlockCube instance;
    @SideOnly(Side.CLIENT)
    private RenderInfo override;

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

    public BlockCube() {
        super(Material.rock);
        setBlockName("railcraft.cube");
        setResistance(20);
        setHardness(5);
        setStepSound(RailcraftSound.getInstance());

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        setHarvestLevel("pickaxe", 1, EnumCube.COKE_BLOCK.ordinal());
        setHarvestLevel("pickaxe", 1, EnumCube.ABYSSAL_STONE.ordinal());
        setHarvestLevel("pickaxe", 2, EnumCube.STEEL_BLOCK.ordinal());
        setHarvestLevel("pickaxe", 1, EnumCube.CONCRETE_BLOCK.ordinal());
        setHarvestLevel("axe", 0, EnumCube.CREOSOTE_BLOCK.ordinal());
        setHarvestLevel("shovel", 3, EnumCube.CRUSHED_OBSIDIAN.ordinal());
    }

    @Override
    public float getBlockHardness(World world, int i, int j, int k) {
        int meta = world.getBlockMetadata(i, j, k);
        return EnumCube.fromOrdinal(meta).getHardness();
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (EnumCube cube : EnumCube.VALUES) {
            if (!cube.isEnabled() || cube == EnumCube.CONCRETE_BLOCK || cube == EnumCube.CREOSOTE_BLOCK)
                continue;
            cube.setIcon(iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(cube.getTag())));
        }
        EnumCube.CONCRETE_BLOCK.setIcon(iconRegister.registerIcon("railcraft:concrete"));
        EnumCube.CREOSOTE_BLOCK.setIcon(iconRegister.registerIcon("railcraft:post.wood"));
    }

    @SideOnly(Side.CLIENT)
    public void setTextureOverride(RenderInfo info) {
        override = info;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (override != null)
            return override.getBlockTextureFromSide(side);
        IIcon icon = EnumCube.fromOrdinal(meta).getIcon();
        if (icon == null)
            return EnumCube.CONCRETE_BLOCK.getIcon();
        return icon;
    }

    public static String getBlockNameFromMetadata(int meta) {
        return EnumCube.fromOrdinal(meta).getTag();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        EnumCube.fromOrdinal(meta).getBlockDef().onNeighborBlockChange(world, x, y, z, block);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        EnumCube.fromOrdinal(meta).getBlockDef().updateTick(world, x, y, z, rand);
    }

    @Override
    public void onPostBlockPlaced(World world, int i, int j, int k, int meta) {
        EnumCube.fromOrdinal(meta).getBlockDef().onBlockPlaced(world, i, j, k);
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random rand) {
        int meta = world.getBlockMetadata(i, j, k);

        EnumCube.fromOrdinal(meta).getBlockDef().randomDisplayTick(world, i, j, k, rand);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        int meta = world.getBlockMetadata(i, j, k);

        EnumCube.fromOrdinal(meta).getBlockDef().onBlockAdded(world, i, j, k);
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
        EnumCube.fromOrdinal(meta).getBlockDef().onBlockRemoval(world, i, j, k);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int i, int j, int k, boolean willHarvest) {
        int meta = world.getBlockMetadata(i, j, k);
        return EnumCube.fromOrdinal(meta).getBlockDef().removedByPlayer(world, player, i, j, k);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        if (override != null) return true;
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int i, int j, int k) {
        int meta = world.getBlockMetadata(i, j, k);
        return EnumCube.fromOrdinal(meta).getBlockDef().canCreatureSpawn(type, world, i, j, k);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumCube type : EnumCube.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int i, int j, int k, double srcX, double srcY, double srcZ) {
        int meta = world.getBlockMetadata(i, j, k);
        return EnumCube.fromOrdinal(meta).getResistance() * 3f / 5f;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        return EnumCube.fromOrdinal(metadata).getBlockDef().getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        return EnumCube.fromOrdinal(metadata).getBlockDef().getFlammability(world, x, y, z, face);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        return EnumCube.fromOrdinal(metadata).getBlockDef().isFlammable(world, x, y, z, face);
    }

}
