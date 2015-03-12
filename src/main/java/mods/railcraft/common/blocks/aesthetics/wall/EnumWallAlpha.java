/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.wall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumWallAlpha implements WallInfo {

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
    FROST_BOUND_BRICK;
    public static final EnumWallAlpha[] VALUES = values();
    private static final List<EnumWallAlpha> creativeList = new ArrayList<EnumWallAlpha>();
    private Block source;
    private int sourceMeta = 0;

    public static void initialize() {
        INFERNAL_BRICK.source = EnumBrick.INFERNAL.getBlock();
        SANDY_BRICK.source = EnumBrick.SANDY.getBlock();
        FROST_BOUND_BRICK.source = EnumBrick.FROSTBOUND.getBlock();
        CONCRETE.source = BlockCube.getBlock();
        CONCRETE.sourceMeta = EnumCube.CONCRETE_BLOCK.ordinal();

        SNOW.source = Blocks.snow;
        ICE.source = Blocks.ice;

        STONE_BRICK.source = Blocks.stonebrick;
        STONE_BRICK.sourceMeta = 0;
        STONE_BRICK_MOSSY.source = Blocks.stonebrick;
        STONE_BRICK_MOSSY.sourceMeta = 1;
        STONE_BRICK_CRACKED.source = Blocks.stonebrick;
        STONE_BRICK_CRACKED.sourceMeta = 2;
        STONE_BRICK_CHISELED.source = Blocks.stonebrick;
        STONE_BRICK_CHISELED.sourceMeta = 3;

        NETHER_BRICK.source = Blocks.nether_brick;

        BRICK.source = Blocks.brick_block;

        SANDSTONE.source = Blocks.sandstone;
        SANDSTONE.sourceMeta = 0;
        SANDSTONE_CHISELED.source = Blocks.sandstone;
        SANDSTONE_CHISELED.sourceMeta = 1;
        SANDSTONE_SMOOTH.source = Blocks.sandstone;
        SANDSTONE_SMOOTH.sourceMeta = 2;

        OBSIDIAN.source = Blocks.obsidian;

//        QUARTZ.source = Block.blockNetherQuartz;
        for (EnumWallAlpha wall : VALUES) {
            if (wall.isEnabled() && wall.source != null)
                if (wall == NETHER_BRICK)
                    CraftingPlugin.addShapedRecipe(wall.getItem(5), "S S", "SSS", 'S', wall.getSourceItem());
                else
                    CraftingPlugin.addShapedRecipe(wall.getItem(6), "SSS", "SSS", 'S', wall.getSourceItem());
        }

        creativeList.addAll(Arrays.asList(VALUES));
    }

    public static WallInfo fromMeta(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static List<EnumWallAlpha> getCreativeList() {
        return creativeList;
    }

    @Override
    public Block getSource() {
        return source;
    }

    @Override
    public int getSourceMeta() {
        return sourceMeta;
    }

    @Override
    public ItemStack getSourceItem() {
        if (source == null) return null;
        return new ItemStack(source, 1, sourceMeta);
    }

    @Override
    public Block getBlock() {
        return BlockRailcraftWall.getBlockAlpha();
    }

    @Override
    public ItemStack getItem() {
        return getItem(1);
    }

    @Override
    public ItemStack getItem(int qty) {
        Block block = getBlock();
        if (block == null) return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    public String getTag() {
        return "tile.railcraft.wall." + name().replace("_", ".").toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean isEnabled() {
        return ModuleManager.isModuleLoaded(Module.STRUCTURES) && RailcraftConfig.isSubBlockEnabled(getTag()) && getBlock() != null;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getHardness();
            default:
                Block block = getSource();
                if (block == null)
                    return Blocks.brick_block.getBlockHardness(world, x, y, z);
                return block.getBlockHardness(world, x, y, z);
        }
    }

    @Override
    public float getExplosionResistance(Entity entity) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
            default:
                Block block = getSource();
                if (block == null)
                    return Blocks.brick_block.getExplosionResistance(entity);
                return block.getExplosionResistance(entity);
        }
    }

}
