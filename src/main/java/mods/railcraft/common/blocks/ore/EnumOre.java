/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.modules.ModuleWorld;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumOre implements IVariantEnumBlock<EnumOre> {

    SULFUR("sulfur"),
    SALTPETER("saltpeter"),
    DARK_DIAMOND("dark_diamond"),
    DARK_EMERALD("dark_emerald"),
    DARK_LAPIS("dark_lapis"),
    POOR_IRON("poor_iron"),
    POOR_GOLD("poor_gold"),
    POOR_COPPER("poor_copper"),
    POOR_TIN("poor_tin"),
    POOR_LEAD("poor_lead"),
    POOR_SILVER("poor_silver"),
    COPPER("copper"),
    TIN("tin"),
    LEAD("lead"),
    SILVER("silver"),;
    public static final EnumOre[] VALUES = values();
    private final Definition def;

    EnumOre(String tag) {
        this.def = new Definition(tag, ModuleWorld.class);
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ORE;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.ore_" + getBaseTag();
    }

    public static EnumOre fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return SULFUR;
        return values()[meta];
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}
