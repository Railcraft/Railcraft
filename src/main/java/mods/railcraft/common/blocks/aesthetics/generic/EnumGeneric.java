/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.generic;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.modules.ModuleBuilding;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.ModuleResources;
import mods.railcraft.common.modules.ModuleWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumGeneric implements IVariantEnumBlock<EnumGeneric> {

    @Deprecated
    BLOCK_COPPER(ModuleResources.class, "copper", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_TIN(ModuleResources.class, "tin", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_LEAD(ModuleResources.class, "lead", new SimpleCube(), 2f, 20f),
    @Deprecated
    BLOCK_STEEL(ModuleResources.class, "steel", new SimpleCube(), 5f, 15f),
    @Deprecated
    BLOCK_CONCRETE(ModuleBuilding.class, "concrete", new SimpleCube(), 3f, 15f),
    BLOCK_CREOSOTE(ModuleBuilding.class, "creosote", new FlammableCube(5, 300), 3f, 10f),
    BLOCK_COKE(ModuleFactory.class, "coke", new FlammableCube(5, 10), 2f, 10f),
    CRUSHED_OBSIDIAN(ModuleResources.class, "crushed_obsidian", new CrushedObsidian(), 2f, 45f),
    STONE_ABYSSAL(ModuleWorld.class, "stone_abyssal", new SimpleCube(), 2f, 10f),
    STONE_QUARRIED(ModuleWorld.class, "stone_quarried", new SimpleCube(), 2f, 10f),
    @Deprecated
    BLOCK_SILVER(ModuleResources.class, "silver", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_BRONZE(ModuleResources.class, "bronze", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_NICKEL(ModuleResources.class, "nickel", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_INVAR(ModuleResources.class, "invar", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_ZINC(ModuleResources.class, "zinc", new SimpleCube(), 3f, 10f),
    @Deprecated
    BLOCK_BRASS(ModuleResources.class, "brass", new SimpleCube(), 3f, 10f),
    ;
    public static final EnumGeneric[] VALUES = values();
    private static final List<EnumGeneric> creativeList = new ArrayList<>();

    static {
        creativeList.add(BLOCK_CREOSOTE);
        creativeList.add(BLOCK_COKE);
        creativeList.add(CRUSHED_OBSIDIAN);
        creativeList.add(STONE_ABYSSAL);
        creativeList.add(STONE_QUARRIED);
    }

    private final SimpleCube blockDef;
    private final float hardness;
    private final float resistance;
    private final Definition def;

    EnumGeneric(@Nullable Class<? extends IRailcraftModule> module, String tag, SimpleCube blockDef, float hardness, float resistance) {
        this.blockDef = blockDef;
        this.hardness = hardness;
        this.resistance = resistance;
        this.def = new Definition(tag, module);
    }

    @Override
    public Definition getDef() {
        return def;
    }

    public static List<EnumGeneric> getCreativeList() {
        return creativeList;
    }

    public static EnumGeneric fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return BLOCK_CONCRETE;
        return VALUES[id];
    }

    @Override
    public String getTag() {
        return "tile.railcraft.generic_" + getBaseTag();
    }

    public SimpleCube getBlockDef() {
        return blockDef;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.GENERIC;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

}
