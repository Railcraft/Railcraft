/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends Enum<M> & IEnumMachine<M>> extends Comparable<M>, IVariantEnumBlock {
    String getBaseTag();

    String getTag();

    Class<? extends IRailcraftModule> getModule();

    /**
     * Block is enabled, but may not be defined yet.
     */
    @Override
    default boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && getContainer().isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    boolean isAvailable();

    boolean isDepreciated();

    PropertyEnum<M> getVariantProperty();

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    default IBlockState getDefaultState() {
        IBlockState state = getContainer().getDefaultState();
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
        Block block = block();
        if (block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    default Block block() {
        return getContainer().block();
    }

    Class<? extends TileMachineBase> getTileClass();

    default String getToolClass() {
        return "pickaxe:2";
    }

    boolean passesLight();

    TileMachineBase getTileEntity();

    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    @Override
    int ordinal();
}
