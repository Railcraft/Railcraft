/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import com.google.common.base.Predicate;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.core.StackFilter;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.UniversalBucket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements IStackFilter {

    ALL {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return true;
        }

    },
    FUEL {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return FuelPlugin.getBurnTime(stack) > 0;
        }

    },
    TRACK {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ITrackItem || (stack.getItem() instanceof ItemBlock && TrackTools.isRailBlock(InvTools.getBlockFromStack(stack))));
        }

    },
    MINECART {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ItemMinecart || stack.getItem() instanceof IMinecartItem);
        }

    },
    BALLAST {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return BallastRegistry.isItemBallast(stack);
        }

    },
    EMPTY_BUCKET {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            if (stack == null)
                return false;
            if (InvTools.isItem(stack, Items.BUCKET) || InvTools.isItemEqual(stack, FluidContainerRegistry.EMPTY_BUCKET))
                return true;
            UniversalBucket uBucket = ForgeModContainer.getInstance().universalBucket;
            return uBucket != null && InvTools.extendsItemClass(stack, UniversalBucket.class) && uBucket.getFluid(stack).amount <= 0;
        }

    },
    FEED {
        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ItemFood || stack.getItem() == Items.WHEAT || stack.getItem() instanceof ItemSeeds);
        }

    };

    public static void initialize() {
        for (StandardStackFilters type : StandardStackFilters.values()) {
            StackFilter.standardFilters.put(type.name(), type);
        }
    }

    @Override
    public abstract boolean apply(@Nullable ItemStack stack);

    @Override
    public final StackFilter and(@Nonnull final Predicate<? super ItemStack>... other) {
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                for (Predicate<? super ItemStack> filter : other) {
                    if (!filter.apply(stack))
                        return false;
                }
                return StandardStackFilters.this.apply(stack);
            }
        };
    }

    @Override
    public final StackFilter or(@Nonnull final Predicate<? super ItemStack>... other) {
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                for (Predicate<? super ItemStack> filter : other) {
                    if (filter.apply(stack))
                        return true;
                }
                return StandardStackFilters.this.apply(stack);
            }
        };
    }

    @Override
    public final StackFilter negate() {
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return !StandardStackFilters.this.apply(stack);
            }
        };
    }

}
