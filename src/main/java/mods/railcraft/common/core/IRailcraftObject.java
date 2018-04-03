/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IRailcraftRegistryEntry;
import mods.railcraft.api.core.IVariantEnum;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * All Railcraft Items and Blocks should implement this.
 *
 * Created by CovertJaguar on 3/14/2016.
 */
public interface IRailcraftObject<T extends IForgeRegistryEntry<T>> extends IRailcraftRegistryEntry<T> {
    T getObject();

    @Nullable
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    default ItemStack getStack() {
        return getStack(1, null);
    }

    default ItemStack getStack(int qty) {
        return getStack(qty, null);
    }

    default ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    default ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        int meta;
        if (variant != null) {
            checkVariant(variant);
            if (!variant.isEnabled())
                return ItemStack.EMPTY;
            meta = variant.ordinal();
        } else
            meta = 0;
        Object obj = getObject();
        if (obj instanceof Item)
            return new ItemStack((Item) obj, qty, meta);
        if (obj instanceof Block)
            return new ItemStack((Block) obj, qty, meta);
        throw new RuntimeException("IRailcraftObject.getStack(int, IVariantEnum) needs to be overridden");
    }

    @Nullable
    default ItemStack getWildcard() {
        Object obj = getObject();
        if (obj instanceof Item)
            return new ItemStack((Item) obj, 1, OreDictionary.WILDCARD_VALUE);
        if (obj instanceof Block)
            return new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE);
        return null;
    }

    default void defineRecipes() {
    }

    default void initializeDefinition() {
    }

    default void finalizeDefinition() {
    }

    @SideOnly(Side.CLIENT)
    default void initializeClient() {
    }

    default void checkVariant(@Nullable IVariantEnum variant) {
        Class<?> clazz = variant == null ? null : variant.getClass();
        if (clazz != null && clazz.isAnonymousClass())
            clazz = clazz.getEnclosingClass();
        if (getVariantEnum() != clazz)
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
        return getRegistryName().getResourcePath();
    }

    @Override
    default ResourceLocation getRegistryName(IVariantEnum variant) {
        checkVariant(variant);
        return IRailcraftRegistryEntry.super.getRegistryName(variant);
    }
}
