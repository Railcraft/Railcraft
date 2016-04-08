/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumOre implements IStringSerializable, IVariantEnum<EnumOre> {

    SULFUR("sulfur"),
    SALTPETER("saltpeter"),
    DARK_DIAMOND("dark.diamond"),
    DARK_EMERALD("dark.emerald"),
    DARK_LAPIS("dark.lapis"),
    FIRESTONE("firestone"),
    WATERSTONE("waterstone"),
    POOR_IRON("poor.iron"),
    POOR_GOLD("poor.gold"),
    POOR_COPPER("poor.copper"),
    POOR_TIN("poor.tin"),
    POOR_LEAD("poor.lead"),;
    public static final EnumOre[] VALUES = values();
    private final String tag;
    private boolean depreciated;

    static {
        WATERSTONE.depreciated = true;
    }

    EnumOre(String tag) {
        this.tag = tag;
    }

    @Nullable
    @Override
    public Block getBlock() {
        return BlockOre.getBlock();
    }

    @Nullable
    @Override
    public IBlockState getState() {
        return null;
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public String getTag() {
        return "tile.railcraft.ore." + tag;
    }

    public ItemStack getItem(int qty) {
        return new ItemStack(BlockOre.getBlock(), qty, ordinal());
    }

    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(ModuleWorld.class) && BlockOre.getBlock() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    public boolean isDepreciated() {
        return depreciated;
    }

    public static EnumOre fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return SULFUR;
        return values()[meta];
    }

    @Override
    public String getName() {
        return tag;
    }
}
