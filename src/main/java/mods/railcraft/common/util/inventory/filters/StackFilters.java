/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.filters;

import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInventoryComposite;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 3/31/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class StackFilters {
    private StackFilters() {
    }

    /**
     * Matches against the provided ItemStack.
     */
    public static Predicate<ItemStack> of(final ItemStack stack) {
        return stack1 -> InvTools.isItemEqual(stack1, stack);
    }

    /**
     * Matches against the provided class/interface.
     */
    public static Predicate<ItemStack> of(final Class<?> itemClass) {
        return stack -> !InvTools.isEmpty(stack) && itemClass.isAssignableFrom(stack.getItem().getClass());
    }

    /**
     * Matches against the provided Item.
     */
    public static Predicate<ItemStack> of(final Item item) {
        return stack -> !InvTools.isEmpty(stack) && stack.getItem() == item;
    }

    /**
     * Matches against the provided Item.
     */
    public static Predicate<ItemStack> of(final RailcraftItems item) {
        return stack -> !InvTools.isEmpty(stack) && item.isEqual(stack);
    }

    /**
     * Matches against the provided Item.
     */
    public static Predicate<ItemStack> of(final Block block) {
        return stack -> !InvTools.isEmpty(stack) && stack.getItem() == Item.getItemFromBlock(block);
    }

    /**
     * Matches against the provided ItemStacks. If the Item class extends IFilterItem then it will pass the check to the item.
     */
    public static Predicate<ItemStack> anyMatch(final ItemStack... filters) {
        return anyMatch(Arrays.asList(filters));
    }

    /**
     * Matches against the provided ItemStacks. If the Item class extends IFilterItem then it will pass the check to the item.
     */
    public static Predicate<ItemStack> anyMatch(final Collection<ItemStack> filters) {
        return stack -> filters.stream().anyMatch(f -> InvTools.matchesFilter(f, stack));
    }

    /**
     * Matches against the provided Inventory. If the Item class extends IFilterItem then it will pass the check to the item.
     */
    public static Predicate<ItemStack> anyMatch(final IInventoryComposite inv) {
        return stack -> inv.streamStacks().anyMatch(f -> InvTools.matchesFilter(f, stack));
    }

    /**
     * Matches against the provided ItemStacks.
     *
     * If no ItemStacks are provided to match against, it returns true.
     */
    public static Predicate<ItemStack> anyOf(final ItemStack... stacks) {
        return anyOf(Arrays.asList(stacks));
    }

    /**
     * Matches against the provided ItemStacks.
     *
     * If no ItemStacks are provided to match against, it returns true.
     */
    public static Predicate<ItemStack> anyOf(final Collection<ItemStack> stacks) {
        return stack -> stacks.isEmpty() || stacks.stream().allMatch(InvTools::isEmpty) || InvTools.isItemEqual(stack, stacks);
    }

    public static Predicate<ItemStack> none() {
        return itemStack -> false;
    }

    /**
     * Matches only if the given ItemStack does not match any of the provided ItemStacks.
     *
     * Returns false if the ItemStack being matched is null and true if the stacks to match against is empty/nulled.
     */
    public static Predicate<ItemStack> noneOf(final ItemStack... stacks) {
        return noneOf(Arrays.asList(stacks));
    }

    /**
     * Matches only if the given ItemStack does not match any of the provided ItemStacks.
     *
     * Returns false if the ItemStack being matched is null and true if the stacks to match against is empty/nulled.
     */
    public static Predicate<ItemStack> noneOf(final Collection<ItemStack> stacks) {
        return stack -> {
            if (InvTools.isEmpty(stack))
                return false;
            return stacks.stream()
                    .filter(InvTools::nonEmpty)
                    .noneMatch(filter -> InvTools.isItemEqual(stack, filter));
        };
    }

    /**
     * Matches if the given ItemStack is registered as a specific OreType in the Ore Dictionary.
     */
    public static Predicate<ItemStack> ofOreType(final String oreTag) {
        return stack -> OreDictPlugin.isOreType(oreTag, stack);
    }

    public static Predicate<ItemStack> ofSize(final int size) {
        return stack -> InvTools.sizeOf(stack) == size;
    }

    public static Predicate<ItemStack> singleton() {
        return stack -> InvTools.sizeOf(stack) == 1;
    }

    public static Predicate<ItemStack> nonEmpty() {
        return InvTools::nonEmpty;
    }

    /**
     * Matches if the Inventory contains the given ItemStack.
     */
    public static Predicate<ItemStack> containedIn(final IInventoryComposite inv) {
        return inv::contains;
    }

    /**
     * Matches if the Inventory has room and accepts the given ItemStack
     */
    public static Predicate<ItemStack> roomIn(final IInventoryComposite inv) {
        return inv::canFit;
    }

    /**
     * Matches if the ItemStack matches the given cart.
     */
    public static Predicate<ItemStack> isCart(@Nullable final EntityMinecart cart) {
        return stack -> {
            if (InvTools.isEmpty(stack))
                return false;
            if (cart == null)
                return false;
            if (cart instanceof IMinecart) {
                if (stack.hasDisplayName())
                    return ((IMinecart) cart).doesCartMatchFilter(stack, cart) && stack.getDisplayName().equals(cart.getCartItem().getDisplayName());
                return ((IMinecart) cart).doesCartMatchFilter(stack, cart);
            }
            ItemStack cartItem = cart.getCartItem();
            return !InvTools.isEmpty(stack) && InvTools.isCartItemEqual(stack, cartItem, true);
        };
    }
}
