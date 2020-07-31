/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.metals.EnumMetal;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import org.jetbrains.annotations.Nullable;

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

    ABYSSAL_POLISHED("abyssal_block", () -> BrickTheme.ABYSSAL.getState(BrickVariant.POLISHED)),
    ABYSSAL_BRICK("abyssal_brick", () -> BrickTheme.ABYSSAL.getState(BrickVariant.PAVER)),
    ABYSSAL_COBBLE("abyssal_cobble", () -> BrickTheme.ABYSSAL.getState(BrickVariant.COBBLE)),
    ABYSSAL_FITTED("abyssal_fitted", () -> BrickTheme.ABYSSAL.getState(BrickVariant.BRICK)),

    BLEACHEDBONE_POLISHED("bleached_bone_block", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.POLISHED)),
    BLEACHEDBONE_BRICK("bleached_bone_brick", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.PAVER)),
    BLEACHEDBONE_COBBLE("bleached_bone_cobble", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.COBBLE)),
    BLEACHEDBONE_FITTED("bleached_bone_fitted", () -> BrickTheme.BLEACHEDBONE.getState(BrickVariant.BRICK)),

    BLOODSTAINED_POLISHED("bloodstained_block", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.POLISHED)),
    BLOODSTAINED_BRICK("bloodstained_brick", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.PAVER)),
    BLOODSTAINED_COBBLE("bloodstained_cobble", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.COBBLE)),
    BLOODSTAINED_FITTED("bloodstained_fitted", () -> BrickTheme.BLOODSTAINED.getState(BrickVariant.BRICK)),

    FROSTBOUND_BLOCK("frost_bound_block", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.POLISHED)),
    FROSTBOUND_BRICK("frost_bound_brick", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.PAVER)),
    FROSTBOUND_COBBLE("frost_bound_cobble", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.COBBLE)),
    FROSTBOUND_FITTED("frost_bound_fitted", () -> BrickTheme.FROSTBOUND.getState(BrickVariant.BRICK)),

    INFERNAL_BLOCK("infernal_block", () -> BrickTheme.INFERNAL.getState(BrickVariant.POLISHED)),
    INFERNAL_BRICK("infernal_brick", () -> BrickTheme.INFERNAL.getState(BrickVariant.PAVER)),
    INFERNAL_COBBLE("infernal_cobble", () -> BrickTheme.INFERNAL.getState(BrickVariant.COBBLE)),
    INFERNAL_FITTED("infernal_fitted", () -> BrickTheme.INFERNAL.getState(BrickVariant.BRICK)),

    NETHER_BLOCK("nether_block", () -> BrickTheme.NETHER.getState(BrickVariant.POLISHED)),
    NETHER_BRICK("nether_brick", Blocks.NETHER_BRICK::getDefaultState),
    NETHER_COBBLE("nether_cobble", () -> BrickTheme.NETHER.getState(BrickVariant.COBBLE)),
    NETHER_FITTED("nether_fitted", () -> BrickTheme.NETHER.getState(BrickVariant.BRICK)),

    RED_NETHER_BLOCK("red_nether_block", () -> BrickTheme.NETHER.getState(BrickVariant.POLISHED)),
    RED_NETHER_BRICK("red_nether_brick", Blocks.RED_NETHER_BRICK::getDefaultState),
    RED_NETHER_COBBLE("red_nether_cobble", () -> BrickTheme.NETHER.getState(BrickVariant.COBBLE)),
    RED_NETHER_FITTED("red_nether_fitted", () -> BrickTheme.NETHER.getState(BrickVariant.BRICK)),

    QUARRIED_BLOCK("quarried_block", () -> BrickTheme.QUARRIED.getState(BrickVariant.POLISHED)),
    QUARRIED_BRICK("quarried_brick", () -> BrickTheme.QUARRIED.getState(BrickVariant.PAVER)),
    QUARRIED_COBBLE("quarried_cobble", () -> BrickTheme.QUARRIED.getState(BrickVariant.COBBLE)),
    QUARRIED_FITTED("quarried_fitted", () -> BrickTheme.QUARRIED.getState(BrickVariant.BRICK)),

    SANDY_BLOCK("sandy_block", () -> BrickTheme.SANDY.getState(BrickVariant.POLISHED)),
    SANDY_BRICK("sandy_brick", () -> BrickTheme.SANDY.getState(BrickVariant.PAVER)),
    SANDY_COBBLE("sandy_cobble", () -> BrickTheme.SANDY.getState(BrickVariant.COBBLE)),
    SANDY_FITTED("sandy_fitted", () -> BrickTheme.SANDY.getState(BrickVariant.BRICK)),

    RED_SANDY_BLOCK("red_sandy_block", () -> BrickTheme.BADLANDS.getState(BrickVariant.POLISHED)),
    RED_SANDY_BRICK("red_sandy_brick", () -> BrickTheme.BADLANDS.getState(BrickVariant.PAVER)),
    RED_SANDY_COBBLE("red_sandy_cobble", () -> BrickTheme.BADLANDS.getState(BrickVariant.COBBLE)),
    RED_SANDY_FITTED("red_sandy_fitted", () -> BrickTheme.BADLANDS.getState(BrickVariant.BRICK)),

    ANDESITE_BLOCK("andesite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)),
    ANDESITE_BRICK("andesite_brick", () -> BrickTheme.ANDESITE.getState(BrickVariant.PAVER)),
    ANDESITE_COBBLE("andesite_cobble", () -> BrickTheme.ANDESITE.getState(BrickVariant.COBBLE)),
    ANDESITE_FITTED("andesite_fitted", () -> BrickTheme.ANDESITE.getState(BrickVariant.BRICK)),

    DIORITE_BLOCK("diorite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)),
    DIORITE_BRICK("diorite_brick", () -> BrickTheme.DIORITE.getState(BrickVariant.PAVER)),
    DIORITE_COBBLE("diorite_cobble", () -> BrickTheme.DIORITE.getState(BrickVariant.COBBLE)),
    DIORITE_FITTED("diorite_fitted", () -> BrickTheme.DIORITE.getState(BrickVariant.BRICK)),

    GRANITE_BLOCK("granite_block", () -> Blocks.STONE.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.GRANITE_SMOOTH)),
    GRANITE_BRICK("granite_brick", () -> BrickTheme.DIORITE.getState(BrickVariant.PAVER)),
    GRANITE_COBBLE("granite_cobble", () -> BrickTheme.DIORITE.getState(BrickVariant.COBBLE)),
    GRANITE_FITTED("granite_fitted", () -> BrickTheme.DIORITE.getState(BrickVariant.BRICK)),

    SNOW("snow", Blocks.SNOW::getDefaultState),
    ICE("ice", Blocks.ICE::getDefaultState),
    PACKED_ICE("packed_ice", Blocks.PACKED_ICE::getDefaultState),

    IRON("iron", Blocks.IRON_BLOCK::getDefaultState),
    STEEL("steel", EnumMetal.BLOCK_STEEL::getDefaultState),
    COPPER("copper", EnumMetal.BLOCK_COPPER::getDefaultState),
    TIN("tin", EnumMetal.BLOCK_TIN::getDefaultState),
    LEAD("lead", EnumMetal.BLOCK_LEAD::getDefaultState),
    GOLD("gold", Blocks.GOLD_BLOCK::getDefaultState),
    BRONZE("bronze", EnumMetal.BLOCK_BRONZE::getDefaultState),
    NICKEL("nickel", EnumMetal.BLOCK_NICKEL::getDefaultState),
    INVAR("invar", EnumMetal.BLOCK_INVAR::getDefaultState),
    ZINC("zinc", EnumMetal.BLOCK_ZINC::getDefaultState),
    BRASS("brass", EnumMetal.BLOCK_BRASS::getDefaultState),

    DIAMOND("diamond", Blocks.DIAMOND_BLOCK::getDefaultState),

    CONCRETE("reinforced_concrete", RailcraftBlocks.REINFORCED_CONCRETE::getDefaultState),
    CREOSOTE("creosote", EnumGeneric.BLOCK_CREOSOTE::getDefaultState),

    NO_MAT("no_mat", () -> null);
    public static final String MATERIAL_KEY = "mat";
    public static final IUnlistedProperty<Materials> MATERIAL_PROPERTY = net.minecraftforge.common.property.Properties.toUnlisted(PropertyEnum.create("material", Materials.class));
    public static final Map<String, Materials> NAMES = new HashMap<>();
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
    private @Nullable IBlockState state;
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
        BRONZE.oreTag = "blockBronze";
        NICKEL.oreTag = "blockNickel";
        INVAR.oreTag = "blockInvar";
        ZINC.oreTag = "blockZinc";
        BRASS.oreTag = "blockBrass";

        CONCRETE.sound = SoundType.STONE;

        CREOSOTE.sound = SoundType.WOOD;

        OBSIDIAN_CRUSHED.sound = SoundType.GROUND;

        COPPER.sound = SoundType.METAL;
        TIN.sound = SoundType.METAL;
        LEAD.sound = SoundType.METAL;
        STEEL.sound = SoundType.METAL;
        BRONZE.sound = SoundType.METAL;
        NICKEL.sound = SoundType.METAL;
        INVAR.sound = SoundType.METAL;
        ZINC.sound = SoundType.METAL;
        BRASS.sound = SoundType.METAL;

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


    public static ItemStack getStack(Block block, int qty, @Nullable IVariantEnum variant) {
        ((IRailcraftObject) block).checkVariant(variant);
        ItemStack stack = new ItemStack(block, qty);
        if (variant != null)
            tagItemStack(stack, MATERIAL_KEY, (Materials) variant);
        return stack;
    }

    public static void tagItemStack(ItemStack stack, String key, Materials material) {
        if (InvTools.isEmpty(stack))
            return;
        NBTTagCompound nbt = stack.getOrCreateSubCompound(Railcraft.MOD_ID);
        nbt.setString(key, material.getName());
    }

    public static Materials from(ItemStack stack, String key) {
        if (InvTools.isEmpty(stack))
            return getPlaceholder();
        NBTTagCompound nbt = stack.getOrCreateSubCompound(Railcraft.MOD_ID);
        if (nbt.hasKey(key))
            return fromName(nbt.getString(key));
        Materials material = OLD_ORDINALS.inverse().get(stack.getItemDamage());
        if (material != null)
            return material;
        return getPlaceholder();
    }

    public @Nullable IBlockState getState() {
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
    public @Nullable String getOreTag() {
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

    public ItemStack getSourceItem() {
        IBlockState state = getState();
        if (state == null) return ItemStack.EMPTY;
        return new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
    }

    public Ingredient getCraftingEquivalent() {
        if (oreTag != null) return new OreIngredient(oreTag);
        return Ingredient.fromStacks(getSourceItem());
    }

    public boolean isTransparent() {
        return this == ICE;
    }

    public float getBlockHardness(World world, BlockPos pos) {
        switch (this) {
            case CREOSOTE:
                return EnumGeneric.BLOCK_CREOSOTE.getHardness();
            case OBSIDIAN_CRUSHED:
                return EnumGeneric.CRUSHED_OBSIDIAN.getHardness();
            case COPPER:
                return EnumMetal.BLOCK_COPPER.getHardness();
            case TIN:
                return EnumMetal.BLOCK_TIN.getHardness();
            case LEAD:
                return EnumMetal.BLOCK_LEAD.getHardness();
            case STEEL:
                return EnumMetal.BLOCK_STEEL.getHardness();
            case BRONZE:
                return EnumMetal.BLOCK_BRONZE.getHardness();
            case NICKEL:
                return EnumMetal.BLOCK_NICKEL.getHardness();
            case INVAR:
                return EnumMetal.BLOCK_INVAR.getHardness();
            case ZINC:
                return EnumMetal.BLOCK_ZINC.getHardness();
            case BRASS:
                return EnumMetal.BLOCK_BRASS.getHardness();
            default:
                IBlockState state = getState();
                if (state == null)
                    return Blocks.BRICK_BLOCK.getDefaultState().getBlockHardness(world, pos);
                return state.getBlockHardness(world, pos);
        }
    }

    public float getExplosionResistance(@Nullable Entity entity) {
        switch (this) {
            case CREOSOTE:
                return EnumGeneric.BLOCK_CREOSOTE.getResistance() * 3f / 5f;
            case OBSIDIAN_CRUSHED:
                return EnumGeneric.CRUSHED_OBSIDIAN.getResistance() * 3f / 5f;
            case COPPER:
                return EnumMetal.BLOCK_COPPER.getResistance() * 3f / 5f;
            case TIN:
                return EnumMetal.BLOCK_TIN.getResistance() * 3f / 5f;
            case LEAD:
                return EnumMetal.BLOCK_LEAD.getResistance() * 3f / 5f;
            case STEEL:
                return EnumMetal.BLOCK_STEEL.getResistance() * 3f / 5f;
            case BRONZE:
                return EnumMetal.BLOCK_BRONZE.getResistance() * 3f / 5f;
            case NICKEL:
                return EnumMetal.BLOCK_NICKEL.getResistance() * 3f / 5f;
            case INVAR:
                return EnumMetal.BLOCK_INVAR.getResistance() * 3f / 5f;
            case ZINC:
                return EnumMetal.BLOCK_ZINC.getResistance() * 3f / 5f;
            case BRASS:
                return EnumMetal.BLOCK_BRASS.getResistance() * 3f / 5f;
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
}
