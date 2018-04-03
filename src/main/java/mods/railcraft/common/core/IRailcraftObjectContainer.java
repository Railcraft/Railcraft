/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This interface is mainly to ensure that RailcraftBlocks and RailcraftItems have similar syntax.
 *
 * Created by CovertJaguar on 4/13/2016.
 */
public interface IRailcraftObjectContainer<T extends IRailcraftObject<?>> extends IRailcraftRecipeIngredient {
    class Definition {
        public final Set<Class<? extends IRailcraftModule>> modules = new HashSet<>();
        private final InitializationConditional conditions = new InitializationConditional();
        private final String tag;
        @Nullable
        private final Supplier<?> altRecipeObject;
        public final ResourceLocation registryName;

        public Definition(IRailcraftObjectContainer<?> obj, String tag, @Nullable Supplier<?> altRecipeObject) {
            this.tag = tag;
            this.altRecipeObject = altRecipeObject;
            registryName = new ResourceLocation(obj.getResourceDomain() + ":" + tag);
            conditions.add(c -> !modules.isEmpty(), () -> "it has no module");
        }
    }

    Definition getDef();

    default ResourceLocation getRegistryName() {
        return getDef().registryName;
    }

    default InitializationConditional conditions() {
        return getDef().conditions;
    }

    /**
     * Register the item. Call {@link IRailcraftObject#initializeDefinition()} in this part!
     */
    default void register() {
    }

    /**
     * To be called in batch by the client proxy.
     */
    @SideOnly(Side.CLIENT)
    default void initializeClient() {
        getObject().ifPresent(IRailcraftObject::initializeClient);
    }

    /**
     * Called in the module event handlers.
     */
    default void defineRecipes() {
        getObject().ifPresent(IRailcraftObject::defineRecipes);
    }

    /**
     * Called in the module event handlers.
     */
    default void finalizeDefinition() {
        getObject().ifPresent(IRailcraftObject::finalizeDefinition);
    }

    default boolean isEqual(@Nullable ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return false;
        // java generics broke so we must use function that takes object
        return getObject().map((Object obj) -> {
            if (obj instanceof Item) {
                return obj == stack.getItem();
            } else if (obj instanceof Block) {
                return InvTools.getBlockFromStack(stack) == obj;
            }
            return false;
        }).orElse(false);
    }

    default String getResourceDomain() {
        return RailcraftConstants.RESOURCE_DOMAIN;
    }

    default String getBaseTag() {
        return getDef().tag;
    }

    default ItemStack getWildcard() {
        return getObject().map(o -> o.getWildcard()).orElse(null);
    }

    default ItemStack getStack() {
        return getStack(1);
    }

    default ItemStack getStack(int qty) {
        return getStack(qty, null);
    }

//    @Nullable
//    default ItemStack getStack(int qty, int meta) {
//        return getObject().map(o -> o.getStack(qty, meta)).orElse(null);
//    }

    default ItemStack getStack(@Nullable IVariantEnum variant) {
        return getStack(1, variant);
    }

    default ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return getObject().map(o -> o.getStack(qty, variant)).orElse(ItemStack.EMPTY);
    }

    Optional<T> getObject();

    @Override
    @Nullable
    @Deprecated
    default Object getRecipeObject() {
        return getRecipeObject(null);
    }

    @Override
    @Nullable
    @Deprecated // Use ingredient
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        Object obj = getObject().map(o -> {
            o.checkVariant(variant);
            return o.getRecipeObject(variant);
        }).orElse(null);
        if (obj == null && variant != null)
            obj = variant.getAlternate(this);
        if (obj == null && getDef().altRecipeObject != null)
            obj = getDef().altRecipeObject.get();
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    default Ingredient getIngredient() {
        Object object = getRecipeObject();
        Ingredient result = CraftingHelper.getIngredient(object);
        return result == null ? Ingredient.EMPTY : result;
    }

    default boolean isEnabled() {
        return getDef().conditions.test(this);
    }

    default boolean isLoaded() {
        return getObject().isPresent();
    }

    /**
     * Set the modules that this object belongs to. Each object must have at least one module. If the module is disabled,
     * this method will not get called, thus the object cannot get registered. The module may be kept for
     * debug use, etc.
     *
     * @param source The module that loads this object
     */
    default void addedBy(Class<? extends IRailcraftModule> source) {
        getDef().modules.add(source);
    }
}
