/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleFactory;
import mods.railcraft.common.modules.ModuleStructures;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumGeneric implements IVariantEnumBlock {

    BLOCK_COPPER(ModuleFactory.class, "copper", new SimpleCube(), 3f, 10f),
    BLOCK_TIN(ModuleFactory.class, "tin", new SimpleCube(), 3f, 10f),
    BLOCK_LEAD(ModuleFactory.class, "lead", new SimpleCube(), 2f, 20f),
    BLOCK_STEEL(ModuleFactory.class, "steel", new SimpleCube(), 5f, 15f),
    BLOCK_CONCRETE(ModuleStructures.class, "concrete", new SimpleCube(), 3f, 15f),
    BLOCK_CREOSOTE(ModuleStructures.class, "creosote", new FlammableCube(5, 300), 3f, 10f),
    BLOCK_COKE(ModuleFactory.class, "coke", new FlammableCube(5, 10), 2f, 10f),
    CRUSHED_OBSIDIAN(ModuleFactory.class, "crushed.obsidian", new CrushedObsidian(), 2f, 45f),
    STONE_ABYSSAL(ModuleWorld.class, "stone.abyssal", new SimpleCube(), 2f, 10f),
    STONE_QUARRIED(ModuleWorld.class, "stone.quarried", new SimpleCube(), 2f, 10f),;
    public static final EnumGeneric[] VALUES = values();
    private static final List<EnumGeneric> creativeList = new ArrayList<EnumGeneric>();

    static {
        creativeList.add(BLOCK_COPPER);
        creativeList.add(BLOCK_TIN);
        creativeList.add(BLOCK_LEAD);
        creativeList.add(BLOCK_STEEL);
        creativeList.add(BLOCK_CONCRETE);
        creativeList.add(BLOCK_CREOSOTE);
        creativeList.add(BLOCK_COKE);
        creativeList.add(CRUSHED_OBSIDIAN);
        creativeList.add(STONE_ABYSSAL);
        creativeList.add(STONE_QUARRIED);
    }

    @Nullable
    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final SimpleCube blockDef;
    private final float hardness;
    private final float resistance;

    EnumGeneric(@Nullable Class<? extends IRailcraftModule> module, String tag, SimpleCube blockDef, float hardness, float resistance) {
        this.module = module;
        this.tag = tag;
        this.blockDef = blockDef;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public static List<EnumGeneric> getCreativeList() {
        return creativeList;
    }

    public static EnumGeneric fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return BLOCK_CONCRETE;
        return VALUES[id];
    }

    @Nullable
    @Override
    public Object getAlternate(String objectTag) {
        return null;
    }

    @Nullable
    public Class<? extends IRailcraftModule> getModule() {
        return module;
    }

    public String getTag() {
        return "tile.railcraft.generic." + tag;
    }

    public SimpleCube getBlockDef() {
        return blockDef;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.generic;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    @Override
    public boolean isEnabled() {
        return getModule() != null && RailcraftModuleManager.isModuleEnabled(getModule()) && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    @Nullable
    public ItemStack getStack() {
        return getStack(1);
    }

    @Nullable
    public ItemStack getStack(int qty) {
        if (!isEnabled())
            return null;
        Block block = getBlock();
        if (block != null)
            return new ItemStack(block, qty, ordinal());
        return null;
    }

    @Nonnull
    @Override
    public String getName() {
        return tag.replace(".", "_");
    }
}
