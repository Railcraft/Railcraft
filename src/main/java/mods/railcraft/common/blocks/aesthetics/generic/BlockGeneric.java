/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.generic;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static mods.railcraft.common.blocks.aesthetics.metals.EnumMetal.*;

@BlockMeta.Variant(EnumGeneric.class)
public class BlockGeneric extends BlockRailcraftSubtyped<EnumGeneric> {

    public BlockGeneric() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumGeneric.BLOCK_COKE));
        setResistance(20);
        setHardness(5);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);

        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
    }

    @Override
    public void initializeDefinition() {
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumGeneric.BLOCK_COKE);
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumGeneric.STONE_ABYSSAL);
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumGeneric.STONE_QUARRIED);

        HarvestPlugin.setStateHarvestLevel("axe", 0, EnumGeneric.BLOCK_CREOSOTE);
        HarvestPlugin.setStateHarvestLevel("shovel", 3, EnumGeneric.CRUSHED_OBSIDIAN);

        EntityTunnelBore.addMineableBlock(this);

        ForestryPlugin.addBackpackItem("forestry.miner", EnumGeneric.BLOCK_COKE.getStack());

        ForestryPlugin.addBackpackItem("forestry.builder", EnumGeneric.BLOCK_CREOSOTE.getStack());

        ForestryPlugin.addBackpackItem("forestry.digger", EnumGeneric.STONE_ABYSSAL.getStack());
        ForestryPlugin.addBackpackItem("forestry.digger", EnumGeneric.STONE_QUARRIED.getStack());

        for (EnumGeneric block : EnumGeneric.VALUES) {
            MicroBlockPlugin.addMicroBlockCandidate(this, block.ordinal());
        }

        OreDictionary.registerOre("blockFuelCoke", EnumGeneric.BLOCK_COKE.getStack());
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(getVariantEnumProperty(), (EnumGeneric) variant);
        }
        return state;
    }

    public static BlockGeneric getBlock() {
        return (BlockGeneric) RailcraftBlocks.GENERIC.block();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(getVariantEnumProperty(), EnumGeneric.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty());
    }

    private EnumGeneric getVariant(IBlockAccess world, BlockPos pos) {
        return getVariant(WorldPlugin.getBlockState(world, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getVariant(blockState).getHardness();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        getVariant(state).getBlockDef().onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        getVariant(state).getBlockDef().updateTick(world, pos, rand);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        getVariant(stateIn).getBlockDef().randomDisplayTick(worldIn, pos, rand);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        getVariant(state).getBlockDef().onBlockAdded(world, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return getVariant(state).getBlockDef().removedByPlayer(world, player, pos);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return getVariant(state).getBlockDef().canCreatureSpawn(type, world, pos);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumGeneric type : EnumGeneric.getCreativeList()) {
            if (type.isEnabled())
                CreativePlugin.addToList(list, type.getStack());
        }
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
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
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getVariant(world, pos).getBlockDef().isFlammable(world, pos, face);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (getVariant(state)) {
            case BLOCK_CREOSOTE:
                return SoundType.WOOD;
            case CRUSHED_OBSIDIAN:
                return SoundType.GROUND;
            case BLOCK_COKE:
                return SoundType.STONE;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        EnumGeneric generic = getVariant(state);
        IBlockState newState = null;
        switch (generic) {
            case BLOCK_CONCRETE:
                newState = RailcraftBlocks.REINFORCED_CONCRETE.getDefaultState();
                break;
            case BLOCK_BRASS:
                newState = RailcraftBlocks.METAL.getState(BLOCK_BRASS);
                break;
            case BLOCK_BRONZE:
                newState = RailcraftBlocks.METAL.getState(BLOCK_BRONZE);
                break;
            case BLOCK_COPPER:
                newState = RailcraftBlocks.METAL.getState(BLOCK_COPPER);
                break;
            case BLOCK_INVAR:
                newState = RailcraftBlocks.METAL.getState(BLOCK_INVAR);
                break;
            case BLOCK_LEAD:
                newState = RailcraftBlocks.METAL.getState(BLOCK_LEAD);
                break;
            case BLOCK_NICKEL:
                newState = RailcraftBlocks.METAL.getState(BLOCK_NICKEL);
                break;
            case BLOCK_SILVER:
                newState = RailcraftBlocks.METAL.getState(BLOCK_SILVER);
                break;
            case BLOCK_STEEL:
                newState = RailcraftBlocks.METAL.getState(BLOCK_STEEL);
                break;
            case BLOCK_TIN:
                newState = RailcraftBlocks.METAL.getState(BLOCK_TIN);
                break;
            case BLOCK_ZINC:
                newState = RailcraftBlocks.METAL.getState(BLOCK_ZINC);
                break;

        }
        if (newState != null)
            WorldPlugin.setBlockState(worldIn, pos, newState);
    }
}
