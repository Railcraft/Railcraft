/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import net.minecraft.item.ItemStack;
import mods.railcraft.api.core.items.IStackFilter;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ComplexStackFilter {

    public static IStackFilter and(IStackFilter... filters) {
        return new AndFilter(filters);
    }

    public static IStackFilter or(IStackFilter... filters) {
        return new OrFilter(filters);
    }

    public static IStackFilter not(IStackFilter filter) {
        return new NotFilter(filter);
    }

    private ComplexStackFilter() {
    }

    private static class AndFilter implements IStackFilter {

        private final IStackFilter[] filters;

        private AndFilter(IStackFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean matches(ItemStack stack) {
            if (stack == null)
                return false;
            for (IStackFilter filter : filters) {
                if (!filter.matches(stack))
                    return false;
            }
            return true;
        }

    }

    private static class OrFilter implements IStackFilter {

        private final IStackFilter[] filters;

        private OrFilter(IStackFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean matches(ItemStack stack) {
            if (stack == null)
                return false;
            for (IStackFilter filter : filters) {
                if (filter.matches(stack))
                    return true;
            }
            return false;
        }

    }

    private static class NotFilter implements IStackFilter {

        private final IStackFilter filter;

        private NotFilter(IStackFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean matches(ItemStack stack) {
            if (stack == null)
                return false;
            return !filter.matches(stack);
        }

    }

}
