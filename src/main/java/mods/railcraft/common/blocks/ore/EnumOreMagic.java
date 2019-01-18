/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.modules.ModuleWorld;

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
        if (meta < 0 || meta >= VALUES.length)
            return FIRESTONE;
        return VALUES[meta];
    }
}
