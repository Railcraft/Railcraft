/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumOre implements IVariantEnumBlock {

    SULFUR("sulfur"),
    SALTPETER("saltpeter"),
    DARK_DIAMOND("dark.diamond"),
    DARK_EMERALD("dark.emerald"),
    DARK_LAPIS("dark.lapis"),
    POOR_IRON("poor.iron"),
    POOR_GOLD("poor.gold"),
    POOR_COPPER("poor.copper"),
    POOR_TIN("poor.tin"),
    POOR_LEAD("poor.lead"),
    FIRESTONE("firestone"),;
    public static final EnumOre[] VALUES = values();
    private final String tag;

    EnumOre(String tag) {
        this.tag = tag;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ore;
    }

    @Nullable
    @Override
    public IBlockState getState() {
        if (getBlock() == null)
            return null;
        return getBlock().getDefaultState().withProperty(BlockOre.VARIANT, this);
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

    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(ModuleWorld.class) && BlockOre.getBlock() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    public boolean isDepreciated() {
        return false;
    }

    public static EnumOre fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return SULFUR;
        return values()[meta];
    }

    @Override
    public String getName() {
        return tag.replace(".","_");
    }

    @Nullable
    @Override
    public Object getAlternate(String objectTag) {
        return null;
    }
}
