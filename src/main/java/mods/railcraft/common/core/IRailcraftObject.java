/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IVariantEnum;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * All Railcraft Items and Blocks should implement this.
 *
 * Created by CovertJaguar on 3/14/2016.
 */
public interface IRailcraftObject<T> extends IForgeRegistryEntry<T> {
    T getObject();

    @Nullable
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nullable
    default ItemStack getStack() {
        return getStack(1, null);
    }

    @Nullable
    default ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Nullable
    default ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        int meta;
        if (variant != null) {
            checkVariant(variant);
            meta = variant.ordinal();
        } else
            meta = 0;
        if (this instanceof Item)
            return new ItemStack((Item) this, qty, meta);
        if (this instanceof Block)
            return new ItemStack((Block) this, qty, meta);
        return null;
    }

    @Nullable
    default ItemStack getStack(int qty, int meta) {
        if (this instanceof Item)
            return new ItemStack((Item) this, qty, meta);
        if (this instanceof Block)
            return new ItemStack((Block) this, qty, meta);
        return null;
    }

    default void defineRecipes() {
    }

    default void initializeDefinintion() {
    }

    default void finalizeDefinition() {
    }

    @SideOnly(Side.CLIENT)
    default void initializeClient() {
    }

    default void checkVariant(@Nullable IVariantEnum variant) {
        if (getVariantEnum() != (variant == null ? null : variant.getClass()))
            throw new RuntimeException("Incorrect Variant object used.");
    }

    @Nullable
    default Class<? extends IVariantEnum> getVariantEnum() {
        return null;
    }

    @Nullable
    default IVariantEnum[] getVariants() {
        Class<? extends IVariantEnum> variantEnum = getVariantEnum();
        if (variantEnum != null) {
            return variantEnum.getEnumConstants();
        }
        return null;
    }

    default String getResourcePath() {
        return ((IForgeRegistryEntry) this).getRegistryName().getResourcePath();
    }
}
