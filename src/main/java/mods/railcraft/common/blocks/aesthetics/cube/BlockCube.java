/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.cube;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockCube extends Block {

    public static final PropertyEnum<EnumCube> VARIANT = PropertyEnum.create("variant", EnumCube.class);

    private static BlockCube instance;
    @SideOnly(Side.CLIENT)
    private RenderInfo override;

    public BlockCube() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumCube.COKE_BLOCK));
        setUnlocalizedName("railcraft.cube");
        setResistance(20);
        setHardness(5);
        setSoundType(RailcraftSound.getInstance());

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static BlockCube getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("cube")) {
                instance = new BlockCube();
                RailcraftRegistry.register(instance, ItemCube.class);

                HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumCube.COKE_BLOCK);
                HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumCube.ABYSSAL_STONE);
                HarvestPlugin.setStateHarvestLevel("pickaxe", 2, EnumCube.STEEL_BLOCK);
                HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumCube.CONCRETE_BLOCK);
                HarvestPlugin.setStateHarvestLevel("axe", 0, EnumCube.CREOSOTE_BLOCK);
                HarvestPlugin.setStateHarvestLevel("shovel", 3, EnumCube.CRUSHED_OBSIDIAN);

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

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumCube.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    private EnumCube getVariant(IBlockAccess world, BlockPos pos) {
        return getVariant(WorldPlugin.getBlockState(world, pos));
    }

    private EnumCube getVariant(IBlockState state) {
        return state.getValue(VARIANT);
    }

    IBlockState getState(EnumCube cube) {
        return getDefaultState().withProperty(VARIANT, cube);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getVariant(blockState).getHardness();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @SideOnly(Side.CLIENT)
    public void setTextureOverride(RenderInfo info) {
        override = info;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        getVariant(world, pos).getBlockDef().onNeighborBlockChange(world, pos, state, neighborBlock);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        getVariant(world, pos).getBlockDef().updateTick(world, pos, rand);
    }

    @Nonnull
    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getVariant(world, pos).getBlockDef().onBlockPlaced(world, pos);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        getVariant(stateIn).getBlockDef().randomDisplayTick(worldIn, pos, rand);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        getVariant(world, pos).getBlockDef().onBlockAdded(world, pos);
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        getVariant(world, pos).getBlockDef().onBlockRemoval(world, pos);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        return getVariant(world, pos).getBlockDef().removedByPlayer(world, player, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
        return override != null || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return getVariant(world, pos).getBlockDef().canCreatureSpawn(type, world, pos);
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumCube type : EnumCube.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nonnull Entity exploder, Explosion explosion) {
        return getVariant(world, pos).getResistance() * 3f / 5f;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getVariant(world, pos).getBlockDef().getFireSpreadSpeed(world, pos, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getVariant(world, pos).getBlockDef().getFlammability(world, pos, face);
    }

    @Override
    public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return getVariant(world, pos).getBlockDef().isFlammable(world, pos, face);
    }

}
