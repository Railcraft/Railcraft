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

import mods.railcraft.api.core.items.StackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
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

    public static StackFilter of(@Nonnull final ItemStack stack) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return InvTools.isItemEqual(stack, stack);
            }
        };
    }

    public static StackFilter anyOf(@Nonnull final ItemStack... stacks) {
        return anyOf(Arrays.asList(stacks));
    }

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

    public static StackFilter noneOf(@Nonnull final ItemStack... stacks) {
        return anyOf(Arrays.asList(stacks));
    }

    public static StackFilter noneOf(@Nonnull final Collection<ItemStack> stacks) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return stack != null && !InvTools.isItemEqual(stack, stacks);
            }
        };
    }

    public static StackFilter containedIn(@Nonnull final IInventory inv) {
        return new StackFilter() {

            @Override
            public boolean apply(final ItemStack stack) {
                return InvTools.containsItem(inv, stack);
            }
        };
    }
}
