/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.lantern;

import com.google.common.collect.BiMap;
import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

import static mods.railcraft.common.blocks.aesthetics.BlockMaterial.*;
import static net.minecraft.util.EnumParticleTypes.FLAME;
import static net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL;

public class BlockLantern extends Block {
    public static final BiMap<Integer, BlockMaterial> STONE_LANTERN;
    public static final BiMap<Integer, BlockMaterial> METAL_LANTERN;
    private static final float SELECT = 2 * 0.0625f;
    static BlockLantern stone;
    static BlockLantern metal;

    static {
        STONE_LANTERN = CollectionTools.createIndexedLookupTable(
                ABYSSAL_BLOCK,
                BLEACHEDBONE_BLOCK,
                BLOODSTAINED_BLOCK,
                FROSTBOUND_BLOCK,
                INFERNAL_BLOCK,
                NETHER_BLOCK,
                QUARRIED_BLOCK,
                SANDY_BLOCK,
                SANDSTONE_SMOOTH,
                STONE_BRICK
        );
        METAL_LANTERN = CollectionTools.createIndexedLookupTable(
                IRON,
                GOLD,
                COPPER,
                TIN,
                LEAD,
                STEEL
        );
    }

    private final PropertyEnum<BlockMaterial> variantProperty;
    private final BiMap<Integer, BlockMaterial> variants;

    public BlockLantern(BiMap<Integer, BlockMaterial> variants) {
        super(Material.REDSTONE_LIGHT);
        this.variantProperty = PropertyEnum.create("variant", BlockMaterial.class, variants.values());
        setDefaultState(blockState.getBaseState().withProperty(variantProperty, variants.get(0)));
        setSoundType(SoundType.STONE);
        this.variants = variants;
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5);
        setResistance(15);
//        useNeighborBrightness[id] = false;
        fullBlock = false;
        lightOpacity = 0;
        setLightLevel(0.9375F);
        setHarvestLevel("pickaxe", 0);
    }

    public static BlockLantern getBlockStone() {
        return stone;
    }

    public static BlockLantern getBlockMetal() {
        return metal;
    }

    public static String getTag(BlockMaterial mat) {
        return "tile.railcraft.lantern." + mat.getLocalizationSuffix();
    }

    public static ItemStack findItem(BlockMaterial mat) {
        return findItem(mat, 1);
    }

    public static ItemStack findItem(BlockMaterial mat, int qty) {
        if (STONE_LANTERN.containsValue(mat))
            return stone.getItem(mat, qty);
        if (METAL_LANTERN.containsValue(mat))
            return metal.getItem(mat, qty);
        return null;
    }

    public boolean isAvailable(BlockMaterial mat) {
        return RailcraftModuleManager.isModuleEnabled(ModuleStructures.class) && RailcraftConfig.isSubBlockEnabled(getTag(mat)) && variantProperty.getAllowedValues().contains(mat);
    }

    public PropertyEnum<BlockMaterial> getVariantProperty() {
        return variantProperty;
    }

    public BlockMaterial getVariant(IBlockState state) {
        return state.getValue(getVariantProperty());
    }

    public ItemStack getItem(BlockMaterial mat) {
        return getItem(mat, 1);
    }

    public ItemStack getItem(IBlockState state) {
        return getItem(state, 1);
    }

    public ItemStack getItem(IBlockState state, int qty) {
        return getItem(getVariant(state), qty);
    }

    public ItemStack getItem(BlockMaterial mat, int qty) {
        return new ItemStack(this, qty, variants.inverse().get(mat));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return getItem(state);
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlockMaterial mat : BlockMaterial.CREATIVE_LIST) {
            if (isAvailable(mat))
                list.add(getItem(mat));
        }
    }

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

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return variants.inverse().get(getVariant(state));
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, @Nonnull Random random) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return ParticleHelper.addHitEffects(worldObj, this, target, manager, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, this, pos, state, manager, null);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Nonnull
    @Override
    public MapColor getMapColor(IBlockState state) {
        IBlockState matState = getVariant(state).getState();
        if (matState == null)
            return EnumColor.YELLOW.getMapColor();
        return matState.getBlock().getMapColor(matState);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(getVariantProperty(), variants.get(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return variants.inverse().get(getVariant(state));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }
}
