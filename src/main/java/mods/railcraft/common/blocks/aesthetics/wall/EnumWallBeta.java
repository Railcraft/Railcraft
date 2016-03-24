/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.wall;

import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumWallBeta implements WallInfo {

    QUARTZ,
    QUARTZ_CHISELED,
    IRON,
    GOLD,
    DIAMOND,
    ABYSSAL_BRICK,
    QUARRIED_BRICK,
    BLOODSTAINED_BRICK,
    BLEACHEDBONE_BRICK,;
    public static final EnumWallBeta[] VALUES = values();
    private static final List<EnumWallBeta> creativeList = new ArrayList<EnumWallBeta>();
    private Block source;
    private int sourceMeta = 0;

    public static void initialize() {
        QUARTZ.sourceMeta = 0;
        QUARTZ.source = Blocks.quartz_block;
        QUARTZ_CHISELED.sourceMeta = 1;
        QUARTZ_CHISELED.source = Blocks.quartz_block;

        IRON.source = Blocks.iron_block;
        GOLD.source = Blocks.gold_block;
        DIAMOND.source = Blocks.diamond_block;

        ABYSSAL_BRICK.source = BrickTheme.ABYSSAL.getBlock();
        QUARRIED_BRICK.source = BrickTheme.QUARRIED.getBlock();
        BLOODSTAINED_BRICK.source = BrickTheme.BLOODSTAINED.getBlock();
        BLEACHEDBONE_BRICK.source = BrickTheme.BLEACHEDBONE.getBlock();

        for (EnumWallBeta wall : VALUES) {
            if (wall.isEnabled() && wall.source != null)
                CraftingPlugin.addShapedRecipe(wall.getItem(6), "SSS", "SSS", 'S', wall.getSourceItem());
        }

        creativeList.addAll(Arrays.asList(VALUES));
    }

    public static WallInfo fromMeta(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static List<EnumWallBeta> getCreativeList() {
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
        return BlockRailcraftWall.getBlockBeta();
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
    public float getBlockHardness(World world, BlockPos pos) {
        Block block = getSource();
        if (block == null)
            return Blocks.brick_block.getBlockHardness(world, pos);
        return block.getBlockHardness(world, pos);
    }

    @Override
    public float getExplosionResistance(Entity entity) {
        Block block = getSource();
        if (block == null)
            return Blocks.brick_block.getExplosionResistance(entity);
        return block.getExplosionResistance(entity);
    }

}
