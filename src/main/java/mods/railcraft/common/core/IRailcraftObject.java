/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IRailcraftRegistryEntry;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * All Railcraft Items and Blocks should implement this.
 *
 * Created by CovertJaguar on 3/14/2016.
 */
public interface IRailcraftObject<T extends IForgeRegistryEntry<T>> extends IRailcraftRegistryEntry<T>, IIngredientSource {
    T getObject();

    @Override
    default Ingredient getIngredient() {
        return CraftingHelper.getIngredient(getObject());
    }

    @Override
    default Ingredient getIngredient(@Nullable IVariantEnum variant) {
        checkVariant(variant);
        String oreTag = getOreTag(variant);
        if (oreTag != null)
            return Ingredients.from(oreTag);
        if (variant != null)
            return Ingredients.from(getStack(variant));
        return Ingredients.from(getWildcard());
    }

    default @Nullable String getOreTag(@Nullable IVariantEnum variant) {
        return null;
    }

    @Override
    default ItemStack getStack() {
        return getStack(1, null);
    }

    @Override
    default ItemStack getStack(int qty) {
        return getStack(qty, null);
    }

    default ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    default ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        if (qty <= 0)
            return ItemStack.EMPTY;
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

    default ItemStack getWildcard() {
        Object obj = getObject();
        if (obj instanceof Item)
            return new ItemStack((Item) obj, 1, OreDictionary.WILDCARD_VALUE);
        if (obj instanceof Block)
            return new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE);
        return ItemStack.EMPTY;
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
        if (getVariantEnumClass() != clazz)
            throw new RuntimeException("Incorrect Variant object used.");
    }

    default @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return null;
    }

    default @Nullable IVariantEnum[] getVariants() {
        Class<? extends IVariantEnum> variantEnum = getVariantEnumClass();
        if (variantEnum != null) {
            return variantEnum.getEnumConstants();
        }
        return null;
    }

    default String getPath() {
        return Objects.requireNonNull(getRegistryName()).getPath();
    }

    @Override
    default ResourceLocation getRegistryName(IVariantEnum variant) {
        checkVariant(variant);
        return IRailcraftRegistryEntry.super.getRegistryName(variant);
    }
}
