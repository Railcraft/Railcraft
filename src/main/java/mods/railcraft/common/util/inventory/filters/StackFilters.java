/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.util.inventory.filters;

import mods.railcraft.api.core.StackFilter;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

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
    public static StackFilter of(@Nonnull final ItemStack stack) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return InvTools.isItemEqual(stack, stack);
            }
        };
    }

    /**
     * Matches against the provided Item.
     */
    public static StackFilter of(@Nonnull final Class<? extends Item> itemClass) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return stack != null && stack.getItem() != null && itemClass.isAssignableFrom(stack.getItem().getClass());
            }
        };
    }

    /**
     * Matches against the provided ItemStacks.
     *
     * If no ItemStacks are provided to match against, it returns true.
     */
    public static StackFilter anyOf(@Nonnull final ItemStack... stacks) {
        return anyOf(Arrays.asList(stacks));
    }

    /**
     * Matches against the provided ItemStacks.
     *
     * If no ItemStacks are provided to match against, it returns true.
     */
    public static StackFilter anyOf(@Nonnull final Collection<ItemStack> stacks) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return stacks.isEmpty() || !hasFilter() || InvTools.isItemEqual(stack, stacks);
            }

            boolean hasFilter() {
                for (ItemStack filter : stacks) {
                    if (filter != null)
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * Matches only if the given ItemStack does not match any of the provided ItemStacks.
     *
     * Returns false if the ItemStack being matched is null and true if the stacks to match against is empty/nulled.
     */
    public static StackFilter noneOf(@Nonnull final ItemStack... stacks) {
        return anyOf(Arrays.asList(stacks));
    }

    /**
     * Matches only if the given ItemStack does not match any of the provided ItemStacks.
     *
     * Returns false if the ItemStack being matched is null and true if the stacks to match against is empty/nulled.
     */
    public static StackFilter noneOf(@Nonnull final Collection<ItemStack> stacks) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                if (stack == null)
                    return false;
                for (ItemStack filter : stacks) {
                    if (filter == null)
                        continue;
                    if (InvTools.isItemEqual(stack, filter))
                        return false;
                }
                return true;
            }
        };
    }

    /**
     * Matches if the given ItemStack is registered as a specific OreType in the Ore Dictionary.
     */
    public static StackFilter ofOreType(@Nonnull final String oreTag) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return OreDictPlugin.isOreType(oreTag, stack);
            }
        };
    }

    /**
     * Matches if the Inventory contains the given ItemStack.
     */
    public static StackFilter containedIn(@Nonnull final IInventory inv) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return InvTools.containsItem(inv, stack);
            }
        };
    }
}
