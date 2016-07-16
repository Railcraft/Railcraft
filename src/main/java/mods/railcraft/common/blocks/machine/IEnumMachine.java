/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IBlockContainer;
import mods.railcraft.common.blocks.IStateContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends Enum<M> & IEnumMachine<M>> extends Comparable<M>, IStringSerializable, IBlockContainer, IStateContainer {
    String getBaseTag();

    String getTag();

    Class<? extends IRailcraftModule> getModule();

    /**
     * Block is enabled, but may not be defined yet.
     */
    default boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && getBlockContainer().isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    boolean isAvailable();

    boolean isDepreciated();

    PropertyEnum<M> getVariantProperty();

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    default IBlockState getState() {
        IBlockState state = getBlockContainer().getDefaultState();
        if (state != null)
            return state.withProperty(getVariantProperty(), (M) this);
        return null;
    }

    @Nullable
    default ItemStack getItem() {
        return getItem(1);
    }

    @Nullable
    default ItemStack getItem(int qty) {
        Block block = getBlock();
        if (block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    RailcraftBlocks getBlockContainer();

    @Override
    default Block getBlock() {
        return getBlockContainer().block();
    }

    Class<? extends TileMachineBase> getTileClass();

    default String getToolClass() {
        return "pickaxe:2";
    }

    boolean passesLight();

    TileMachineBase getTileEntity();

    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    int ordinal();
}
