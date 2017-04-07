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
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends Enum<M> & IEnumMachine<M>> extends Comparable<M>, IVariantEnumBlock {
    class Definition {
        public final Class<? extends IRailcraftModule>[] modules;
        public final String tag;
        public final Class<? extends TileMachineBase> tile;
        public boolean passesLight;
        public ToolTip tip;
        private Boolean enabled = null;

        @SafeVarargs
        public Definition(String tag, Class<? extends TileMachineBase> tile, Class<? extends IRailcraftModule>... modules) {
            this.modules = modules;
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

    /**
     * Block is enabled, but may not be defined yet.
     */
    @Override
    default boolean isEnabled() {
        Definition def = getDef();
        if (def.enabled == null)
            def.enabled = Arrays.stream(getDef().modules).allMatch(RailcraftModuleManager::isModuleEnabled) && getContainer().isEnabled() && RailcraftConfig.isSubBlockEnabled(getTag());
        return def.enabled;
    }

    default boolean isAvailable() {
        return block() != null && isEnabled();
    }

    default void ifAvailable(Consumer<IEnumMachine<M>> action) {
        if (isAvailable())
            action.accept(this);
    }

    default boolean isDepreciated() {
        return ArrayUtils.isEmpty(getDef().modules);
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
    default ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        Definition def = getDef();
        if (def.tip != null)
            return def.tip;
        String tipTag = getLocalizationTag() + ".tips";
        if (LocalizationPlugin.hasTag(tipTag))
            def.tip = ToolTip.buildToolTip(tipTag);
        return def.tip;
    }

    @Override
    int ordinal();
}
