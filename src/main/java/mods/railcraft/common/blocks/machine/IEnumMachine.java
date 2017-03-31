/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends Enum<M> & IEnumMachine<M>> extends Comparable<M>, IVariantEnumBlock {
    class Definition {
        @Nullable
        public final Class<? extends IRailcraftModule> module;
        public final String tag;
        public final Class<? extends TileMachineBase> tile;
        public boolean passesLight;
        public ToolTip tip;

        public Definition(@Nullable Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
            this.module = module;
            this.tag = tag;
            this.tile = tile;
        }
    }

    Definition getDef();

    default String getBaseTag() {
        return getDef().tag;
    }

    String getTag();

    @Override
    default String getName() {
        return getBaseTag();
    }

    default String getLocalizationTag() {
        return getTag().replace("_", ".");
    }

    @Override
    default String getResourcePathSuffix() {
        return getBaseTag();
    }

    @Nullable
    default Class<? extends IRailcraftModule> getModule() {
        return getDef().module;
    }

    /**
     * Block is enabled, but may not be defined yet.
     */
    @Override
    default boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(getModule()) && getContainer().isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    default boolean isAvailable() {
        return block() != null && isEnabled();
    }

    default void ifAvailable(Consumer<IEnumMachine<M>> action) {
        if (isAvailable())
            action.accept(this);
    }

    default boolean isDepreciated() {
        return getDef().module == null;
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

    default Class<? extends TileMachineBase> getTileClass() {
        return getDef().tile;
    }

    default TileMachineBase getTileEntity() {
        try {
            return getTileClass().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    default String getToolClass() {
        return "pickaxe:2";
    }

    default boolean passesLight() {
        return getDef().passesLight;
    }

    @Nullable
    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    @Override
    int ordinal();
}
