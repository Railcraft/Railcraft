/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraft.util.EnumParticleTypes.FLAME;
import static net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL;

public class BlockLantern extends BlockRailcraft implements IMaterialBlock {
    private static final float SELECT = 2 * 0.0625f;

    public BlockLantern() {
        super(Material.REDSTONE_LIGHT);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
        setHardness(5);
        setResistance(15);
//        useNeighborBrightness[id] = false;
        fullBlock = false;
        lightOpacity = 0;
        setLightLevel(0.9375F);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return Materials.class;
    }

    @Override
    public void finalizeDefinition() {
        super.finalizeDefinition();
        for (Materials mat : Materials.getValidMats()) {
            if (Materials.MAT_SET_FROZEN.contains(mat))
                continue;

            RailcraftRegistry.register(this, mat, getStack(mat));
            ForestryPlugin.addBackpackItem("forestry.builder", getStack(mat));

//            Ingredient slab;
//            if (RailcraftBlocks.SLAB.isEnabled())
//                slab = RailcraftBlocks.SLAB.getIngredient(mat);
//            else
//                slab = mat.getCraftingEquivalent();
////            if (mat == Materials.SANDSTONE)
////                slab = new ItemStack(Blocks.STONE_SLAB, 1, 1);
////            else if (mat == Materials.STONE_BRICK)
////                slab = new ItemStack(Blocks.STONE_SLAB, 1, 0);
//            CraftingPlugin.addShapedRecipe(getStack(mat), " S ", " T ", " S ", 'S', slab, 'T', new ItemStack(Blocks.TORCH));
        }
        MatTools.defineCrusherRecipes(this);
    }

    @Override
    public String getTranslationKey(Materials mat) {
        return "tile.railcraft.lantern." + mat.getLocalizationSuffix();
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return Materials.getStack(this, qty, variant);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return MatTools.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.addAll(Materials.getCreativeList().stream().filter(m -> !Materials.MAT_SET_FROZEN.contains(m)).map(this::getStack).filter(InvTools::nonEmpty).collect(Collectors.toList()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return AABBFactory.start().createBoxForTileAt(pos).expandHorizontally(-SELECT).raiseFloor(2 * 0.0625f).raiseCeiling(-0.0625f).build();
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double dx = pos.getX() + 0.5F;
        double dy = pos.getY() + 0.65F;
        double dz = pos.getZ() + 0.5F;

        worldIn.spawnParticle(SMOKE_NORMAL, dx, dy, dz, 0.0D, 0.0D, 0.0D);
        worldIn.spawnParticle(FLAME, dx, dy, dz, 0.0D, 0.0D, 0.0D);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return MatTools.getDrops(world, pos, state, fortune);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        MatTools.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
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
        return new TileMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return MatTools.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return MatTools.getExplosionResistance(world, pos, exploder, explosion);
    }

    //TODO: fix particles
//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
//        return ParticleHelper.addHitEffects(worldObj, this, target, manager, null);
//    }
//
//    @Override
//    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
//        IBlockState state = WorldPlugin.getBlockState(world, pos);
//        return ParticleHelper.addDestroyEffects(world, this, pos, state, manager, null);
//    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */

    @SuppressWarnings("deprecation")
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return EnumColor.YELLOW.getMapColor();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{Materials.MATERIAL_PROPERTY});
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState actState = (IExtendedBlockState) super.getActualState(state, worldIn, pos);
        return actState.withProperty(Materials.MATERIAL_PROPERTY, MatTools.getMat(worldIn, pos));
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return MatTools.getSound(world, pos);
    }
}
