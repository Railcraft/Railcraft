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

import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import net.minecraft.block.*;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum BlockMaterial implements IBlockMaterial, IStringSerializable {

    SNOW(3, "snow"),
    ICE(4, "ice"),
    PACKED_ICE(5, "packed_ice"),
    IRON(6, "iron"),
    GOLD(7, "gold"),
    DIAMOND(8, "diamond"),
    OBSIDIAN(39, "obsidian"),
    BRICK_BLOCK("brick"),

    STONE_BRICK("stone_brick"),
    STONE_BRICK_CHISELED("stone_brick_chiseled"),
    STONE_BRICK_CRACKED("stone_brick_cracked"),
    STONE_BRICK_MOSSY("stone_brick_mossy"),

    SANDSTONE("sandstone"),
    SANDSTONE_CHISELED("sandstone_chiseled"),
    SANDSTONE_SMOOTH("sandstone_smooth"),

    QUARTZ("quartz"),
    QUARTZ_CHISELED("quartz_chiseled"),

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
    COPPER(40, "copper"),
    TIN(41, "tin"),
    LEAD(42, "lead"),
    STEEL(43, "steel");
    public static final BlockMaterial[] VALUES = values();
    public static final Map<String, BlockMaterial> NAMES = new HashMap<String, BlockMaterial>();
    public static final List<BlockMaterial> creativeList = new ArrayList<BlockMaterial>();
    public static final BlockMaterial[] OLD_WALL1_MATS;
    public static final BlockMaterial[] OLD_WALL2_MATS;
//    public static final BlockMaterial[] WALL_SANDY_MATS;
    public static final EnumSet<BlockMaterial> STAIR_MATS;
    public static final EnumSet<BlockMaterial> SLAB_MATS;
    private static boolean needsInit = true;
    private SoundType sound;
    @Nullable
    private IBlockState state;
    private String oreTag;
    public final int oldOrdinal;
    private final String name;

    static {
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
                BRICK_BLOCK,
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

        STAIR_MATS = EnumSet.complementOf(EnumSet.of(
                STONE_BRICK,
                STONE_BRICK_MOSSY,
                STONE_BRICK_CHISELED,
                STONE_BRICK_CRACKED,
                SANDSTONE,
                SANDSTONE_CHISELED,
                SANDSTONE_SMOOTH
        ));

        SLAB_MATS = EnumSet.complementOf(EnumSet.of(
                STONE_BRICK,
                STONE_BRICK_MOSSY,
                STONE_BRICK_CHISELED,
                STONE_BRICK_CRACKED,
                SANDSTONE,
                SANDSTONE_CHISELED,
                SANDSTONE_SMOOTH
        ));
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

        SNOW.state = Blocks.snow.getDefaultState();
        ICE.state = Blocks.ice.getDefaultState();
        PACKED_ICE.state = Blocks.packed_ice.getDefaultState();
        IRON.state = Blocks.iron_block.getDefaultState();
        IRON.oreTag = "blockIron";
        GOLD.state = Blocks.gold_block.getDefaultState();
        GOLD.oreTag = "blockGold";
        DIAMOND.state = Blocks.diamond_block.getDefaultState();
        DIAMOND.oreTag = "blockDiamond";
        OBSIDIAN.state = Blocks.obsidian.getDefaultState();
        BRICK_BLOCK.state = Blocks.brick_block.getDefaultState();

        STONE_BRICK.state = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
        STONE_BRICK_CHISELED.state = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
        STONE_BRICK_CRACKED.state = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
        STONE_BRICK_MOSSY.state = Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);

        SANDSTONE.state = Blocks.sandstone.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.DEFAULT);
        SANDSTONE_CHISELED.state = Blocks.sandstone.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED);
        SANDSTONE_SMOOTH.state = Blocks.sandstone.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH);

        QUARTZ.state = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT);
        QUARTZ_CHISELED.state = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);

        INFERNAL_BRICK.state = BrickTheme.INFERNAL.getState(BrickVariant.BRICK);
        SANDY_BRICK.state = BrickTheme.SANDY.getState(BrickVariant.BRICK);
        FROSTBOUND_BRICK.state = BrickTheme.FROSTBOUND.getState(BrickVariant.BRICK);
        QUARRIED_BRICK.state = BrickTheme.QUARRIED.getState(BrickVariant.BRICK);
        BLEACHEDBONE_BRICK.state = BrickTheme.BLEACHEDBONE.getState(BrickVariant.BRICK);
        BLOODSTAINED_BRICK.state = BrickTheme.BLOODSTAINED.getState(BrickVariant.BRICK);
        ABYSSAL_BRICK.state = BrickTheme.ABYSSAL.getState(BrickVariant.BRICK);
        NETHER_BRICK.state = Blocks.nether_brick.getDefaultState();

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
                    mat.sound = Block.soundTypeStone;
                    break;
                case CREOSOTE:
                    mat.sound = Block.soundTypeWood;
                    break;
                case COPPER:
                case TIN:
                case LEAD:
                case STEEL:
                    mat.sound = Block.soundTypeMetal;
                    break;
                default:
                    if (mat.state != null)
                        mat.sound = mat.state.getBlock().stepSound;
            }
            if (mat.sound == RailcraftSound.getInstance())
                throw new RuntimeException("Invalid Sound Defined!");
        }


        creativeList.add(SNOW);
        creativeList.add(ICE);
        creativeList.add(PACKED_ICE);
        creativeList.add(IRON);
        creativeList.add(STEEL);
        creativeList.add(COPPER);
        creativeList.add(TIN);
        creativeList.add(LEAD);
        creativeList.add(GOLD);
        creativeList.add(DIAMOND);
        creativeList.add(OBSIDIAN);
        creativeList.add(CONCRETE);
        creativeList.add(CREOSOTE);
        creativeList.add(ABYSSAL_BRICK);
        creativeList.add(ABYSSAL_FITTED);
        creativeList.add(ABYSSAL_BLOCK);
        creativeList.add(ABYSSAL_COBBLE);
        creativeList.add(INFERNAL_BRICK);
        creativeList.add(INFERNAL_FITTED);
        creativeList.add(INFERNAL_BLOCK);
        creativeList.add(INFERNAL_COBBLE);
        creativeList.add(BLOODSTAINED_BRICK);
        creativeList.add(BLOODSTAINED_FITTED);
        creativeList.add(BLOODSTAINED_BLOCK);
        creativeList.add(BLOODSTAINED_COBBLE);
        creativeList.add(SANDY_BRICK);
        creativeList.add(SANDY_FITTED);
        creativeList.add(SANDY_BLOCK);
        creativeList.add(SANDY_COBBLE);
        creativeList.add(BLEACHEDBONE_BRICK);
        creativeList.add(BLEACHEDBONE_FITTED);
        creativeList.add(BLEACHEDBONE_BLOCK);
        creativeList.add(BLEACHEDBONE_COBBLE);
        creativeList.add(NETHER_FITTED);
        creativeList.add(NETHER_BLOCK);
        creativeList.add(NETHER_COBBLE);
        creativeList.add(QUARRIED_BRICK);
        creativeList.add(QUARRIED_FITTED);
        creativeList.add(QUARRIED_BLOCK);
        creativeList.add(QUARRIED_COBBLE);
        creativeList.add(FROSTBOUND_BRICK);
        creativeList.add(FROSTBOUND_FITTED);
        creativeList.add(FROSTBOUND_BLOCK);
        creativeList.add(FROSTBOUND_COBBLE);
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

    @Override
    public IBlockState getState() {
        return state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRegistryName() {
        return "railcraft:" + name;
    }

    @Override
    public String getLocalizationSuffix() {
        return name;
    }

    public String getOreTag() {
        return oreTag;
    }

    @Override
    public SoundType getSound() {
        return sound;
    }

    public ItemStack getSourceItem() {
        if (state == null) return null;
        return new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
    }

    public Object getCraftingEquivalent() {
        if (oreTag != null) return oreTag;
        return getSourceItem();
    }

    @Override
    public boolean isTransparent() {
        return this == ICE;
    }

    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getHardness();
            case CREOSOTE:
                return EnumCube.CONCRETE_BLOCK.getHardness();
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
                    return Blocks.brick_block.getBlockHardness(world, pos);
                return state.getBlock().getBlockHardness(world, pos);
        }
    }

    @Override
    public float getExplosionResistance(Entity entity) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
            case CREOSOTE:
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
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
                    return Blocks.brick_block.getExplosionResistance(entity);
                try {
                    return state.getBlock().getExplosionResistance(entity);
                } catch (RuntimeException ex) {
                    return Blocks.brick_block.getExplosionResistance(entity);
                }
        }
    }

}
