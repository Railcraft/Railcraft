/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class BlockRailcraftStairs extends BlockStairs implements IMaterialBlock {
    public static int currentRenderPass;
    static BlockRailcraftStairs block;

    public BlockRailcraftStairs() {
        super(Blocks.STONEBRICK.getDefaultState());
        setSoundType(RailcraftSoundTypes.OVERRIDE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
        isBlockContainer = true;
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return Materials.class;
    }

    @Override
    public String getUnlocalizedName(Materials mat) {
        return "tile.railcraft.stair." + mat.getLocalizationSuffix();
    }

    @Override
    public void finalizeDefinition() {
        for (Materials mat : Materials.getValidMats()) {
            RailcraftRegistry.register(this, mat, getStack(mat));

            switch (mat) {
                case SNOW:
                case ICE:
                    break;
                default:
                    ForestryPlugin.addBackpackItem("forestry.builder", getStack(mat));
            }

            CraftingPlugin.addRecipe(getStack(4, mat), "S  ", "SS ", "SSS", 'S', mat.getSourceItem());
            ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(getStack(mat), true, false);
            //noinspection ConstantConditions
            recipe.addOutput(mat.getSourceItem(), 1.0f);
        }

        MatTools.defineCrusherRecipes(this);
    }

    @Nonnull
    @Override
    public ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nonnull
    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return Materials.getStack(this, qty, variant);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{FACING, HALF, SHAPE}, new IUnlistedProperty[]{Materials.MATERIAL_PROPERTY});
    }

    @Nonnull
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState actState = (IExtendedBlockState) super.getActualState(state, worldIn, pos);
        return actState.withProperty(Materials.MATERIAL_PROPERTY, MatTools.getMat(worldIn, pos));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return MatTools.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.addAll(Materials.getCreativeList().stream().map(this::getStack).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        return MatTools.getDrops(world, pos, state, fortune);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        MatTools.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
        return world.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileMaterial();
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return MatTools.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nonnull Entity exploder, Explosion explosion) {
        return MatTools.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return MatTools.getSound(world, pos);
    }
    //TODO: fix particles
//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager particleManager) {
//        return ParticleHelper.addHitEffects(worldObj, block, target, particleManager, null);
//    }
//
//    @Override
//    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager) {
//        IBlockState state = WorldPlugin.getBlockState(world, pos);
//        return ParticleHelper.addDestroyEffects(world, block, pos, state, particleManager, null);
//    }

    //TODO: fix?
//    @Nonnull
//    @Override
//    public String getHarvestTool(@Nonnull IBlockState state) {
//        IBlockState matState = state.getValue(Materials.MATERIAL_PROPERTY).getState();
//        if (matState != null)
//            return matState.getBlock().getHarvestTool(matState);
//        return "pickaxe";
//    }
}
