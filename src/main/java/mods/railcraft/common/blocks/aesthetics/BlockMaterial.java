/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum BlockMaterial implements IStringSerializable {

    SNOW(3, "snow"),
    ICE(4, "ice"),
    PACKED_ICE(5, "packed_ice"),
    IRON(6, "iron"),
    GOLD(7, "gold"),
    DIAMOND(8, "diamond"),
    OBSIDIAN(39, "obsidian"),
    BRICK("brick"),

    STONE_BRICK("stone_brick"),
    STONE_BRICK_CHISELED("stone_brick_chiseled"),
    STONE_BRICK_CRACKED("stone_brick_cracked"),
    STONE_BRICK_MOSSY("stone_brick_mossy"),

    SANDSTONE("sandstone"),
    SANDSTONE_CHISELED("sandstone_chiseled"),
    SANDSTONE_SMOOTH("sandstone_smooth"),

    RED_SANDSTONE("red_sandstone"),
    RED_SANDSTONE_CHISELED("red_sandstone_chiseled"),
    RED_SANDSTONE_SMOOTH("red_sandstone_smooth"),

    QUARTZ("quartz"),
    QUARTZ_CHISELED("quartz_chiseled"),

    PURPUR("purpur"),

    SANDY_BRICK(0, "sandy_brick"),
    INFERNAL_BRICK(1, "infernal_brick"),
    FROSTBOUND_BRICK(9, "frost_bound_brick"),
    QUARRIED_BRICK(10, "quarried_brick"),
    BLEACHEDBONE_BRICK(11, "bleached_bone_brick"),
    BLOODSTAINED_BRICK(12, "bloodstained_brick"),
    ABYSSAL_BRICK(13, "abyssal_brick"),
    NETHER_BRICK("nether_brick"),

    SANDY_FITTED(14, "sandy_fitted"),
    INFERNAL_FITTED(15, "infernal_fitted"),
    FROSTBOUND_FITTED(16, "frost_bound_fitted"),
    QUARRIED_FITTED(17, "quarried_fitted"),
    BLEACHEDBONE_FITTED(18, "bleached_bone_fitted"),
    BLOODSTAINED_FITTED(19, "bloodstained_fitted"),
    ABYSSAL_FITTED(20, "abyssal_fitted"),
    NETHER_FITTED(21, "nether_fitted"),

    SANDY_BLOCK(22, "sandy_block"),
    INFERNAL_BLOCK(23, "infernal_block"),
    FROSTBOUND_BLOCK(24, "frost_bound_block"),
    QUARRIED_BLOCK(25, "quarried_block"),
    BLEACHEDBONE_BLOCK(26, "bleached_bone_block"),
    BLOODSTAINED_BLOCK(27, "bloodstained_block"),
    ABYSSAL_BLOCK(28, "abyssal_block"),
    NETHER_BLOCK(29, "nether_block"),

    SANDY_COBBLE(30, "sandy_cobble"),
    INFERNAL_COBBLE(31, "infernal_cobble"),
    FROSTBOUND_COBBLE(32, "frost_bound_cobble"),
    QUARRIED_COBBLE(33, "quarried_cobble"),
    BLEACHEDBONE_COBBLE(34, "bleached_bone_cobble"),
    BLOODSTAINED_COBBLE(35, "bloodstained_cobble"),
    ABYSSAL_COBBLE(36, "abyssal_cobble"),
    NETHER_COBBLE(37, "nether_cobble"),

    CONCRETE(2, "concrete"),
    CREOSOTE(38, "creosote"),
    OBSIDIAN_CRUSHED("crushed_obsidian"),

    COPPER(40, "copper"),
    TIN(41, "tin"),
    LEAD(42, "lead"),
    STEEL(43, "steel");
    public static final BlockMaterial[] VALUES = values();
    public static final Map<String, BlockMaterial> NAMES = new HashMap<String, BlockMaterial>();
    public static final BlockMaterial[] CREATIVE_LIST;
    public static final BlockMaterial[] OLD_WALL1_MATS;
    public static final BlockMaterial[] OLD_WALL2_MATS;
    public static final EnumSet<BlockMaterial> VANILLA_REFINED_MATS;
    public static final BiMap<BlockMaterial, Integer> OLD_ORDINALS;
    private static boolean needsInit = true;
    private SoundType sound = SoundType.STONE;
    @Nullable
    private IBlockState state;
    private String oreTag;
    public final int oldOrdinal;
    private final String name;

    static {
        VANILLA_REFINED_MATS = EnumSet.of(SANDSTONE, RED_SANDSTONE, QUARTZ, NETHER_BRICK, STONE_BRICK, BRICK, PURPUR);

        OLD_WALL1_MATS = new BlockMaterial[]{
                INFERNAL_BRICK,
                SANDY_BRICK,
                CONCRETE,
                SNOW,
                ICE,
                STONE_BRICK,
                STONE_BRICK_MOSSY,
                STONE_BRICK_CRACKED,
                STONE_BRICK_CHISELED,
                NETHER_BRICK,
                BRICK,
                SANDSTONE,
                SANDSTONE_CHISELED,
                SANDSTONE_SMOOTH,
                OBSIDIAN,
                FROSTBOUND_BRICK};

        OLD_WALL2_MATS = new BlockMaterial[]{
                QUARTZ,
                QUARTZ_CHISELED,
                IRON,
                GOLD,
                DIAMOND,
                ABYSSAL_BRICK,
                QUARRIED_BRICK,
                BLOODSTAINED_BRICK,
                BLEACHEDBONE_BRICK};

//        WALL_SANDY_MATS = new BlockMaterial[16] {
//            SANDY_BRICK,
//                    SANDY_FITTED,
//            SANDY_BLOCK,
//                    SANDY_COBBLE,
//        } ;

        OLD_ORDINALS = HashBiMap.create();
        for (BlockMaterial mat : BlockMaterial.VALUES) {
            if (mat.oldOrdinal >= 0) {
                OLD_ORDINALS.put(mat, mat.oldOrdinal);
            }
        }

        CREATIVE_LIST = values();
    }

    BlockMaterial(String name) {
        this(-1, name);
    }

    BlockMaterial(int oldOrdinal, String name) {
        this.oldOrdinal = oldOrdinal;
        this.name = name;
    }

    public static void initialize() {
        if (!needsInit)
            return;
        needsInit = false;

        SNOW.state = Blocks.SNOW.getDefaultState();
        ICE.state = Blocks.ICE.getDefaultState();
        PACKED_ICE.state = Blocks.PACKED_ICE.getDefaultState();
        IRON.state = Blocks.IRON_BLOCK.getDefaultState();
        IRON.oreTag = "blockIron";
        GOLD.state = Blocks.GOLD_BLOCK.getDefaultState();
        GOLD.oreTag = "blockGold";
        DIAMOND.state = Blocks.DIAMOND_BLOCK.getDefaultState();
        DIAMOND.oreTag = "blockDiamond";
        OBSIDIAN.state = Blocks.OBSIDIAN.getDefaultState();
        BRICK.state = Blocks.BRICK_BLOCK.getDefaultState();

        STONE_BRICK.state = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
        STONE_BRICK_CHISELED.state = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
        STONE_BRICK_CRACKED.state = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
        STONE_BRICK_MOSSY.state = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);

        SANDSTONE.state = Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.DEFAULT);
        SANDSTONE_CHISELED.state = Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED);
        SANDSTONE_SMOOTH.state = Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH);

        RED_SANDSTONE.state = Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.DEFAULT);
        RED_SANDSTONE_CHISELED.state = Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED);
        RED_SANDSTONE_SMOOTH.state = Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH);

        QUARTZ.state = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT);
        QUARTZ_CHISELED.state = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);

        INFERNAL_BRICK.state = BrickTheme.INFERNAL.getState(BrickVariant.BRICK);
        SANDY_BRICK.state = BrickTheme.SANDY.getState(BrickVariant.BRICK);
        FROSTBOUND_BRICK.state = BrickTheme.FROSTBOUND.getState(BrickVariant.BRICK);
        QUARRIED_BRICK.state = BrickTheme.QUARRIED.getState(BrickVariant.BRICK);
        BLEACHEDBONE_BRICK.state = BrickTheme.BLEACHEDBONE.getState(BrickVariant.BRICK);
        BLOODSTAINED_BRICK.state = BrickTheme.BLOODSTAINED.getState(BrickVariant.BRICK);
        ABYSSAL_BRICK.state = BrickTheme.ABYSSAL.getState(BrickVariant.BRICK);
        NETHER_BRICK.state = Blocks.NETHER_BRICK.getDefaultState();

        SANDY_FITTED.state = BrickTheme.SANDY.getState(BrickVariant.FITTED);
        INFERNAL_FITTED.state = BrickTheme.INFERNAL.getState(BrickVariant.FITTED);
        FROSTBOUND_FITTED.state = BrickTheme.FROSTBOUND.getState(BrickVariant.FITTED);
        QUARRIED_FITTED.state = BrickTheme.QUARRIED.getState(BrickVariant.FITTED);
        BLEACHEDBONE_FITTED.state = BrickTheme.BLEACHEDBONE.getState(BrickVariant.FITTED);
        BLOODSTAINED_FITTED.state = BrickTheme.BLOODSTAINED.getState(BrickVariant.FITTED);
        ABYSSAL_FITTED.state = BrickTheme.ABYSSAL.getState(BrickVariant.FITTED);
        NETHER_FITTED.state = BrickTheme.NETHER.getState(BrickVariant.FITTED);

        SANDY_BLOCK.state = BrickTheme.SANDY.getState(BrickVariant.BLOCK);
        INFERNAL_BLOCK.state = BrickTheme.INFERNAL.getState(BrickVariant.BLOCK);
        FROSTBOUND_BLOCK.state = BrickTheme.FROSTBOUND.getState(BrickVariant.BLOCK);
        QUARRIED_BLOCK.state = BrickTheme.QUARRIED.getState(BrickVariant.BLOCK);
        BLEACHEDBONE_BLOCK.state = BrickTheme.BLEACHEDBONE.getState(BrickVariant.BLOCK);
        BLOODSTAINED_BLOCK.state = BrickTheme.BLOODSTAINED.getState(BrickVariant.BLOCK);
        ABYSSAL_BLOCK.state = BrickTheme.ABYSSAL.getState(BrickVariant.BLOCK);
        NETHER_BLOCK.state = BrickTheme.NETHER.getState(BrickVariant.BLOCK);

        SANDY_COBBLE.state = BrickTheme.SANDY.getState(BrickVariant.COBBLE);
        INFERNAL_COBBLE.state = BrickTheme.INFERNAL.getState(BrickVariant.COBBLE);
        FROSTBOUND_COBBLE.state = BrickTheme.FROSTBOUND.getState(BrickVariant.COBBLE);
        QUARRIED_COBBLE.state = BrickTheme.QUARRIED.getState(BrickVariant.COBBLE);
        BLEACHEDBONE_COBBLE.state = BrickTheme.BLEACHEDBONE.getState(BrickVariant.COBBLE);
        BLOODSTAINED_COBBLE.state = BrickTheme.BLOODSTAINED.getState(BrickVariant.COBBLE);
        ABYSSAL_COBBLE.state = BrickTheme.ABYSSAL.getState(BrickVariant.COBBLE);
        NETHER_COBBLE.state = BrickTheme.NETHER.getState(BrickVariant.COBBLE);

        CONCRETE.state = EnumCube.CONCRETE_BLOCK.getState();
        CREOSOTE.state = EnumCube.CREOSOTE_BLOCK.getState();
        OBSIDIAN_CRUSHED.state = EnumCube.CRUSHED_OBSIDIAN.getState();

        COPPER.state = EnumCube.COPPER_BLOCK.getState();
        COPPER.oreTag = "blockCopper";
        TIN.state = EnumCube.TIN_BLOCK.getState();
        TIN.oreTag = "blockTin";
        LEAD.state = EnumCube.LEAD_BLOCK.getState();
        LEAD.oreTag = "blockLead";
        STEEL.state = EnumCube.STEEL_BLOCK.getState();
        STEEL.oreTag = "blockSteel";

        for (BlockMaterial mat : VALUES) {
            NAMES.put(mat.name(), mat);
            NAMES.put(mat.name, mat);
            switch (mat) {
                case CONCRETE:
                    mat.sound = SoundType.STONE;
                    break;
                case CREOSOTE:
                    mat.sound = SoundType.WOOD;
                    break;
                case OBSIDIAN_CRUSHED:
                    mat.sound = SoundType.GROUND;
                    break;
                case COPPER:
                case TIN:
                case LEAD:
                case STEEL:
                    mat.sound = SoundType.METAL;
                    break;
                default:
                    if (mat.state != null)
                        mat.sound = mat.state.getBlock().getSoundType();
            }
            throw new RuntimeException("Invalid Sound Defined!");
        }
    }

    @Deprecated
    public static BlockMaterial fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    @Deprecated
    public static BlockMaterial fromName(String name) {
        BlockMaterial mat = NAMES.get(name);
        if (mat != null)
            return mat;
        return SANDY_BRICK;
    }

    @Nullable
    public IBlockState getState() {
        return state;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    public String getRegistryName() {
        return "railcraft:" + name;
    }

    @Nonnull
    public String getLocalizationSuffix() {
        return name;
    }

    public String getOreTag() {
        return oreTag;
    }

    @Nonnull
    public SoundType getSound() {
        return sound;
    }

    @Nullable
    public ItemStack getSourceItem() {
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
                return EnumCube.CONCRETE_BLOCK.getHardness();
            case CREOSOTE:
                return EnumCube.CONCRETE_BLOCK.getHardness();
            case OBSIDIAN_CRUSHED:
                return EnumCube.CRUSHED_OBSIDIAN.getHardness();
            case COPPER:
                return EnumCube.COPPER_BLOCK.getHardness();
            case TIN:
                return EnumCube.TIN_BLOCK.getHardness();
            case LEAD:
                return EnumCube.LEAD_BLOCK.getHardness();
            case STEEL:
                return EnumCube.STEEL_BLOCK.getHardness();
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
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
            case CREOSOTE:
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
            case OBSIDIAN_CRUSHED:
                return EnumCube.CRUSHED_OBSIDIAN.getResistance() * 3f / 5f;
            case COPPER:
                return EnumCube.COPPER_BLOCK.getResistance() * 3f / 5f;
            case TIN:
                return EnumCube.TIN_BLOCK.getResistance() * 3f / 5f;
            case LEAD:
                return EnumCube.LEAD_BLOCK.getResistance() * 3f / 5f;
            case STEEL:
                return EnumCube.STEEL_BLOCK.getResistance() * 3f / 5f;
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

}
