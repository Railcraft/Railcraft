/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.Railcraft;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraft.block.BlockStone.VARIANT;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Materials implements IVariantEnum {

    STONE_BRICK("stone_brick", () -> Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT)),
    STONE_BRICK_CHISELED("stone_brick_chiseled", () -> Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED)),
    STONE_BRICK_CRACKED("stone_brick_cracked", () -> Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED)),
    STONE_BRICK_MOSSY("stone_brick_mossy", () -> Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY)),

    BRICK("brick", Blocks.BRICK_BLOCK::getDefaultState),

    SANDSTONE("sandstone", () -> Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.DEFAULT)),
    SANDSTONE_CHISELED("sandstone_chiseled", () -> Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED)),
    SANDSTONE_SMOOTH("sandstone_smooth", () -> Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH)),

    RED_SANDSTONE("red_sandstone", () -> Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.DEFAULT)),
    RED_SANDSTONE_CHISELED("red_sandstone_chiseled", () -> Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED)),
    RED_SANDSTONE_SMOOTH("red_sandstone_smooth", () -> Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH)),

    QUARTZ("quartz", () -> Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT)),
    QUARTZ_CHISELED("quartz_chiseled", () -> Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED)),

    PURPUR("purpur", Blocks.PURPUR_BLOCK::getDefaultState),

    OBSIDIAN("obsidian", Blocks.OBSIDIAN::getDefaultState),
    OBSIDIAN_CRUSHED("crushed_obsidian", EnumGeneric.CRUSHED_OBSIDIAN::getDefaultState),

    ABYSSAL_BLOCK("abyssal_block", () -> BrickTheme.ABYSSAL.getState(BrickVariant.BLOCK)),
    ABYSSAL_BRICK("abyssal_brick", () -> BrickTheme.ABYSSAL.getState(BrickVariant.BRICK)),
    ABYSSAL_COBBLE("abyssal_cobble", () -> BrickTheme.ABYSSAL.getState(BrickVariant.COBBLE)),
    ABYSSAL_FITTED("abyssal_fitted", () -> BrickTheme.ABYSSAL.getState(BrickVariant.FITTED)),

    BLEACHEDBONE_BLOCK("bleached_bone_block", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.BLOCK)),
    BLEACHEDBONE_BRICK("bleached_bone_brick", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.BRICK)),
    BLEACHEDBONE_COBBLE("bleached_bone_cobble", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.COBBLE)),
    BLEACHEDBONE_FITTED("bleached_bone_fitted", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.FITTED)),

    BLOODSTAINED_BLOCK("bloodstained_block", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.BLOCK)),
    BLOODSTAINED_BRICK("bloodstained_brick", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.BRICK)),
    BLOODSTAINED_COBBLE("bloodstained_cobble", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.COBBLE)),
    BLOODSTAINED_FITTED("bloodstained_fitted", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.FITTED)),

    FROSTBOUND_BLOCK("frost_bound_block", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.BLOCK)),
    FROSTBOUND_BRICK("frost_bound_brick", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.BRICK)),
    FROSTBOUND_COBBLE("frost_bound_cobble", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.COBBLE)),
    FROSTBOUND_FITTED("frost_bound_fitted", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.FITTED)),

    INFERNAL_BLOCK("infernal_block", () -> BrickTheme.INFERNAL.getState(BrickVariant.BLOCK)),
    INFERNAL_BRICK("infernal_brick", () -> BrickTheme.INFERNAL.getState(BrickVariant.BRICK)),
    INFERNAL_COBBLE("infernal_cobble", () -> BrickTheme.INFERNAL.getState(BrickVariant.COBBLE)),
    INFERNAL_FITTED("infernal_fitted", () -> BrickTheme.INFERNAL.getState(BrickVariant.FITTED)),

    NETHER_BLOCK("nether_block", () -> BrickTheme.NETHER.getState(BrickVariant.BLOCK)),
    NETHER_BRICK("nether_brick", Blocks.NETHER_BRICK::getDefaultState),
    NETHER_COBBLE("nether_cobble", () -> BrickTheme.NETHER.getState(BrickVariant.COBBLE)),
    NETHER_FITTED("nether_fitted", () -> BrickTheme.NETHER.getState(BrickVariant.FITTED)),

    RED_NETHER_BLOCK("red_nether_block", () -> BrickTheme.NETHER.getState(BrickVariant.BLOCK)),
    RED_NETHER_BRICK("red_nether_brick", Blocks.RED_NETHER_BRICK::getDefaultState),
    RED_NETHER_COBBLE("red_nether_cobble", () -> BrickTheme.NETHER.getState(BrickVariant.COBBLE)),
    RED_NETHER_FITTED("red_nether_fitted", () -> BrickTheme.NETHER.getState(BrickVariant.FITTED)),

    QUARRIED_BLOCK("quarried_block", () -> BrickTheme.QUARRIED.getState(BrickVariant.BLOCK)),
    QUARRIED_BRICK("quarried_brick", () -> BrickTheme.QUARRIED.getState(BrickVariant.BRICK)),
    QUARRIED_COBBLE("quarried_cobble", () -> BrickTheme.QUARRIED.getState(BrickVariant.COBBLE)),
    QUARRIED_FITTED("quarried_fitted", () -> BrickTheme.QUARRIED.getState(BrickVariant.FITTED)),

    SANDY_BLOCK("sandy_block", () -> BrickTheme.SANDY.getState(BrickVariant.BLOCK)),
    SANDY_BRICK("sandy_brick", () -> BrickTheme.SANDY.getState(BrickVariant.BRICK)),
    SANDY_COBBLE("sandy_cobble", () -> BrickTheme.SANDY.getState(BrickVariant.COBBLE)),
    SANDY_FITTED("sandy_fitted", () -> BrickTheme.SANDY.getState(BrickVariant.FITTED)),

    RED_SANDY_BLOCK("red_sandy_block", () -> BrickTheme.REDSANDY.getState(BrickVariant.BLOCK)),
    RED_SANDY_BRICK("red_sandy_brick", () -> BrickTheme.REDSANDY.getState(BrickVariant.BRICK)),
    RED_SANDY_COBBLE("red_sandy_cobble", () -> BrickTheme.REDSANDY.getState(BrickVariant.COBBLE)),
    RED_SANDY_FITTED("red_sandy_fitted", () -> BrickTheme.REDSANDY.getState(BrickVariant.FITTED)),

    ANDESITE_BLOCK("andesite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)),
    ANDESITE_BRICK("andesite_brick", () -> BrickTheme.ANDESITE.getState(BrickVariant.BRICK)),
    ANDESITE_COBBLE("andesite_cobble", () -> BrickTheme.ANDESITE.getState(BrickVariant.COBBLE)),
    ANDESITE_FITTED("andesite_fitted", () -> BrickTheme.ANDESITE.getState(BrickVariant.FITTED)),

    DIORITE_BLOCK("diorite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)),
    DIORITE_BRICK("diorite_brick", () -> BrickTheme.DIORITE.getState(BrickVariant.BRICK)),
    DIORITE_COBBLE("diorite_cobble", () -> BrickTheme.DIORITE.getState(BrickVariant.COBBLE)),
    DIORITE_FITTED("diorite_fitted", () -> BrickTheme.DIORITE.getState(BrickVariant.FITTED)),

    GRANITE_BLOCK("granite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.GRANITE_SMOOTH)),
    GRANITE_BRICK("granite_brick", () -> BrickTheme.DIORITE.getState(BrickVariant.BRICK)),
    GRANITE_COBBLE("granite_cobble", () -> BrickTheme.DIORITE.getState(BrickVariant.COBBLE)),
    GRANITE_FITTED("granite_fitted", () -> BrickTheme.DIORITE.getState(BrickVariant.FITTED)),

    SNOW("snow", Blocks.SNOW::getDefaultState),
    ICE("ice", Blocks.ICE::getDefaultState),
    PACKED_ICE("packed_ice", Blocks.PACKED_ICE::getDefaultState),

    IRON("iron", Blocks.IRON_BLOCK::getDefaultState),
    STEEL("steel", EnumGeneric.BLOCK_STEEL::getDefaultState),
    COPPER("copper", EnumGeneric.BLOCK_COPPER::getDefaultState),
    TIN("tin", EnumGeneric.BLOCK_TIN::getDefaultState),
    LEAD("lead", EnumGeneric.BLOCK_LEAD::getDefaultState),
    GOLD("gold", Blocks.GOLD_BLOCK::getDefaultState),

    DIAMOND("diamond", Blocks.DIAMOND_BLOCK::getDefaultState),

    CONCRETE("concrete", EnumGeneric.BLOCK_CONCRETE::getDefaultState),
    CREOSOTE("creosote", EnumGeneric.BLOCK_CREOSOTE::getDefaultState),

    NO_MAT("no_mat", () -> null);
    public static final String MATERIAL_KEY = "mat";
    public static final IUnlistedProperty<Materials> MATERIAL_PROPERTY = net.minecraftforge.common.property.Properties.toUnlisted(PropertyEnum.create("material", Materials.class));
    public static final Map<String, Materials> NAMES = new HashMap<String, Materials>();
    //    public static final BlockMaterial[] OLD_WALL1_MATS;
//    public static final BlockMaterial[] OLD_WALL2_MATS;
    public static final EnumSet<Materials> MAT_SET_VANILLA = EnumSet.of(SANDSTONE, RED_SANDSTONE, QUARTZ, NETHER_BRICK, STONE_BRICK, BRICK, PURPUR);
    public static final EnumSet<Materials> MAT_SET_FROZEN = EnumSet.of(SNOW, ICE, PACKED_ICE);
    public static final BiMap<Materials, Integer> OLD_ORDINALS;
    private static final Materials[] VALUES = values();
    private static final List<Materials> CREATIVE_LIST;
    private static boolean needsInit = true;

    static {

//        OLD_WALL1_MATS = new BlockMaterial[]{
//                INFERNAL_BRICK,
//                SANDY_BRICK,
//                CONCRETE,
//                SNOW,
//                ICE,
//                STONE_BRICK,
//                STONE_BRICK_MOSSY,
//                STONE_BRICK_CRACKED,
//                STONE_BRICK_CHISELED,
//                NETHER_BRICK,
//                BRICK,
//                SANDSTONE,
//                SANDSTONE_CHISELED,
//                SANDSTONE_SMOOTH,
//                OBSIDIAN,
//                FROSTBOUND_BRICK};
//
//        OLD_WALL2_MATS = new BlockMaterial[]{
//                QUARTZ,
//                QUARTZ_CHISELED,
//                IRON,
//                GOLD,
//                DIAMOND,
//                ABYSSAL_BRICK,
//                QUARRIED_BRICK,
//                BLOODSTAINED_BRICK,
//                BLEACHEDBONE_BRICK};

//        WALL_SANDY_MATS = new BlockMaterial[16] {
//            SANDY_BRICK,
//                    SANDY_FITTED,
//            SANDY_BLOCK,
//                    SANDY_COBBLE,
//        } ;

        OLD_ORDINALS = HashBiMap.create();
        for (Materials mat : Materials.VALUES) {
            if (mat.oldOrdinal >= 0) {
                OLD_ORDINALS.put(mat, mat.oldOrdinal);
            }
        }

        CREATIVE_LIST = Arrays.asList(values());
    }

    public final int oldOrdinal;
    private final String name;
    private final Supplier<IBlockState> stateSupplier;
    private SoundType sound = SoundType.STONE;
    @Nullable
    private IBlockState state;
    private String oreTag;

    Materials(String name, Supplier<IBlockState> stateSupplier) {
        this(-1, name, stateSupplier);
    }

    Materials(int oldOrdinal, String name, Supplier<IBlockState> stateSupplier) {
        this.oldOrdinal = oldOrdinal;
        this.name = name;
        this.stateSupplier = stateSupplier;
    }

    public static void initialize() {
        if (!needsInit)
            return;
        needsInit = false;

        GameRegistry.registerTileEntity(TileMaterial.class, "RCMaterialTile");

        IRON.oreTag = "blockIron";
        GOLD.oreTag = "blockGold";
        DIAMOND.oreTag = "blockDiamond";

        COPPER.oreTag = "blockCopper";
        TIN.oreTag = "blockTin";
        LEAD.oreTag = "blockLead";
        STEEL.oreTag = "blockSteel";

        CONCRETE.sound = SoundType.STONE;

        CREOSOTE.sound = SoundType.WOOD;

        OBSIDIAN_CRUSHED.sound = SoundType.GROUND;

        COPPER.sound = SoundType.METAL;
        TIN.sound = SoundType.METAL;
        LEAD.sound = SoundType.METAL;
        STEEL.sound = SoundType.METAL;

        for (Materials mat : VALUES) {
            NAMES.put(mat.name(), mat);
            NAMES.put(mat.name, mat);
        }
    }

    @Deprecated
    public static Materials fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static Materials fromName(String name) {
        Materials mat = NAMES.get(name);
        if (mat != null)
            return mat;
        return getPlaceholder();
    }

    public static List<Materials> getValidMats() {
        initialize();
        return Arrays.stream(VALUES).filter(Materials::isSourceValid).collect(Collectors.toList());
    }

    public static List<Materials> getCreativeList() {
        initialize();
        return CREATIVE_LIST.stream().filter(Materials::isSourceValid).collect(Collectors.toList());
    }

    public static Materials getPlaceholder() {
        for (Materials material : VALUES) {
            if (material.isSourceValid())
                return material;
        }
        throw new RuntimeException("this should never happen");
    }

    @Nonnull
    public static ItemStack getStack(Block block, int qty, @Nullable IVariantEnum variant) {
        ((IRailcraftObject) block).checkVariant(variant);
        ItemStack stack = new ItemStack(block, qty);
        if (variant != null)
            tagItemStack(stack, MATERIAL_KEY, (Materials) variant);
        return stack;
    }

    public static void tagItemStack(ItemStack stack, String key, Materials material) {
        if (stack == null)
            return;
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        nbt.setString(key, material.getName());
    }

    public static Materials from(ItemStack stack, String key) {
        if (stack == null)
            return getPlaceholder();
        NBTTagCompound nbt = stack.getSubCompound(Railcraft.MOD_ID, true);
        if (nbt.hasKey(key))
            return fromName(nbt.getString(key));
        Materials material = OLD_ORDINALS.inverse().get(stack.getItemDamage());
        if (material != null)
            return material;
        return getPlaceholder();
    }

    @Nullable
    public IBlockState getState() {
        if (state == null)
            state = stateSupplier.get();
        return state;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getLocalizationSuffix() {
        return name.replace("_", ".");
    }

    @Override
    @Nullable
    public String getOreTag() {
        return oreTag;
    }

    public SoundType getSound() {
        if (sound == null) {
            IBlockState state = getState();
            if (state != null) {
                sound = state.getBlock().getSoundType();
            }
        }
        if (sound != null)
            return sound;
        return SoundType.STONE;
    }

    @Nullable
    public ItemStack getSourceItem() {
        IBlockState state = getState();
        if (state == null) return null;
        return new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
    }

    @Nullable
    public Object getCraftingEquivalent() {
        if (oreTag != null) return oreTag;
        return getSourceItem();
    }

    public boolean isTransparent() {
        return this == ICE;
    }

    public float getBlockHardness(World world, BlockPos pos) {
        switch (this) {
            case CONCRETE:
                return EnumGeneric.BLOCK_CONCRETE.getHardness();
            case CREOSOTE:
                return EnumGeneric.BLOCK_CREOSOTE.getHardness();
            case OBSIDIAN_CRUSHED:
                return EnumGeneric.CRUSHED_OBSIDIAN.getHardness();
            case COPPER:
                return EnumGeneric.BLOCK_COPPER.getHardness();
            case TIN:
                return EnumGeneric.BLOCK_TIN.getHardness();
            case LEAD:
                return EnumGeneric.BLOCK_LEAD.getHardness();
            case STEEL:
                return EnumGeneric.BLOCK_STEEL.getHardness();
            default:
                IBlockState state = getState();
                if (state == null)
                    return Blocks.BRICK_BLOCK.getDefaultState().getBlockHardness(world, pos);
                return state.getBlockHardness(world, pos);
        }
    }

    public float getExplosionResistance(Entity entity) {
        switch (this) {
            case CONCRETE:
                return EnumGeneric.BLOCK_CONCRETE.getResistance() * 3f / 5f;
            case CREOSOTE:
                return EnumGeneric.BLOCK_CREOSOTE.getResistance() * 3f / 5f;
            case OBSIDIAN_CRUSHED:
                return EnumGeneric.CRUSHED_OBSIDIAN.getResistance() * 3f / 5f;
            case COPPER:
                return EnumGeneric.BLOCK_COPPER.getResistance() * 3f / 5f;
            case TIN:
                return EnumGeneric.BLOCK_TIN.getResistance() * 3f / 5f;
            case LEAD:
                return EnumGeneric.BLOCK_LEAD.getResistance() * 3f / 5f;
            case STEEL:
                return EnumGeneric.BLOCK_STEEL.getResistance() * 3f / 5f;
            default:
                IBlockState state = getState();
                if (state == null)
                    return Blocks.BRICK_BLOCK.getExplosionResistance(entity);
                try {
                    return state.getBlock().getExplosionResistance(entity);
                } catch (RuntimeException ex) {
                    return Blocks.BRICK_BLOCK.getExplosionResistance(entity);
                }
        }
    }

    public boolean isSourceValid() {
        return getState() != null;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}
