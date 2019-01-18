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
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.modules.ModuleWorld;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumOreMetalPoor implements IVariantEnumBlock<EnumOreMetalPoor> {


    IRON(Metal.IRON),
    GOLD(Metal.GOLD),
    COPPER(Metal.COPPER),
    TIN(Metal.TIN),
    LEAD(Metal.LEAD),
    SILVER(Metal.SILVER),
    NICKEL(Metal.NICKEL),
    ZINC(Metal.ZINC),;
    public static final EnumOreMetalPoor[] VALUES = values();
    private final Definition def;
    private final Metal metal;

    EnumOreMetalPoor(Metal metal) {
        this.def = new Definition(metal.getName(), ModuleWorld.class);
        this.metal = metal;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ORE_METAL_POOR;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.ore_metal_poor_" + getBaseTag();
    }

    public static EnumOreMetalPoor fromOrdinal(int meta) {
        if (meta < 0 || meta >= VALUES.length)
            return IRON;
        return VALUES[meta];
    }

    public Metal getMetal() {
        return metal;
    }

    @Override
    public @Nullable String getOreTag() {
        return metal.getOreTag(Metal.Form.POOR_ORE);
    }
}
