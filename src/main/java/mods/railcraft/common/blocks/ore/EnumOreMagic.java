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
public enum EnumOreMagic implements IVariantEnumBlock<EnumOreMagic> {

    FIRESTONE("firestone");
    public static final EnumOreMagic[] VALUES = values();
    private final Definition def;

    EnumOreMagic(String tag) {
        this.def = new Definition(tag, ModuleWorld.class);
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ORE_MAGIC;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.ore_magic_" + getBaseTag();
    }

    public static EnumOreMagic fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return FIRESTONE;
        return values()[meta];
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}
