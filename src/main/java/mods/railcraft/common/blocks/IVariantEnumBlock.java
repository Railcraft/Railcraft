/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IContainerBlock;
import mods.railcraft.common.core.IContainerState;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Lets apply some standardization to my variant enums.
 * <p>
 * Created by CovertJaguar on 3/24/2016.
 */
public interface IVariantEnumBlock<M extends Enum<M> & IVariantEnumBlock<M>> extends Comparable<M>, IVariantEnum, IContainerState, IContainerBlock {
    class Definition {
        public final Class<? extends IRailcraftModule>[] modules;
        public final String tag;
        public ToolTip tip;
        private Boolean enabled;

        @SafeVarargs
        public Definition(String tag, Class<? extends IRailcraftModule>... modules) {
            this.modules = modules;
            this.tag = tag;
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

    default void ifAvailable(Consumer<IVariantEnumBlock<M>> action) {
        if (isAvailable())
            action.accept(this);
    }

    @Override
    default boolean isDeprecated() {
        try {
            //noinspection unchecked
            return getClass().getField(((M) this).name()).isAnnotationPresent(Deprecated.class);
        } catch (NoSuchFieldException ignored) {
        }
        return IVariantEnum.super.isDeprecated();
    }

    @Nullable
    default ItemStack getStack() {
        return getStack(1);
    }

    @Nullable
    default ItemStack getStack(int qty) {
        Block block = block();
        if (block == null)
            return InvTools.emptyStack();
        return new ItemStack(block, qty, ordinal());
    }

    IRailcraftBlockContainer getContainer();

    @Nullable
    @Override
    default Block block() {
        return getContainer().block();
    }

    @Nullable
    @Override
    default IBlockState getDefaultState() {
        return getContainer().getState(this);
    }

    default Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
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
