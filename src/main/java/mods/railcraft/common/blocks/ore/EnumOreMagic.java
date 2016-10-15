/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumOreMagic implements IVariantEnumBlock {

    FIRESTONE("firestone");
    public static final EnumOreMagic[] VALUES = values();
    private final String tag;

    EnumOreMagic(String tag) {
        this.tag = tag;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ORE_MAGIC;
    }

    @Nullable
    @Override
    public IBlockState getDefaultState() {
        Block block = block();
        if (block == null)
            return null;
        return block.getDefaultState().withProperty(BlockOreMagic.VARIANT, this);
    }

    public String getTag() {
        return "tile.railcraft.ore_magic_" + tag;
    }

    @Nullable
    public ItemStack getItem() {
        return getItem(1);
    }

    @Nullable
    public ItemStack getItem(int qty) {
        Block block = block();
        if (block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(ModuleWorld.class) && block() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    public static EnumOreMagic fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return FIRESTONE;
        return values()[meta];
    }

    @Override
    public String getName() {
        return tag;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}
