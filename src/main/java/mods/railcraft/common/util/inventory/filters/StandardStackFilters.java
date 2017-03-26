/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import mods.railcraft.api.core.RailcraftStackFilters;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.UniversalBucket;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements Predicate<ItemStack> {

    ALL {
        @Override
        public boolean test(@Nullable ItemStack stack) {
            return true;
        }

    },
    FUEL {
        @Override
        public boolean test(@Nullable ItemStack stack) {
            return FuelPlugin.getBurnTime(stack) > 0;
        }

    },
    TRACK {
        @Override
        public boolean test(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ITrackItem || (stack.getItem() instanceof ItemBlock && TrackTools.isRailBlock(InvTools.getBlockFromStack(stack))));
        }

    },
    MINECART {
        @Override
        public boolean test(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ItemMinecart || stack.getItem() instanceof IMinecartItem);
        }

    },
    BALLAST {
        @Override
        public boolean test(@Nullable ItemStack stack) {
            return BallastRegistry.isItemBallast(stack);
        }

    },
    EMPTY_BUCKET {
        @Override
        public boolean test(@Nullable ItemStack stack) {
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
        public boolean test(@Nullable ItemStack stack) {
            return stack != null && (stack.getItem() instanceof ItemFood || stack.getItem() == Items.WHEAT || stack.getItem() instanceof ItemSeeds);
        }

    };

    public static void initialize() {
        Map<String, Predicate<ItemStack>> filters = new HashMap<>();
        for (StandardStackFilters type : StandardStackFilters.values()) {
            filters.put(type.name(), type);
        }
        RailcraftStackFilters.init(filters);
    }

    @Override
    public abstract boolean test(@Nullable ItemStack stack);

}
