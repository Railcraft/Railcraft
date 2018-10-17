/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.filters;

import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.api.items.ITrackItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements Predicate<@Nullable ItemStack> {

    ALL {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack);
        }

    },
    FUEL {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack) && FuelPlugin.getBurnTime(stack) > 0;
        }

    },
    TRACK {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack) && (stack.getItem() instanceof ITrackItem || (stack.getItem() instanceof ItemBlock && TrackTools.isRailBlock(InvTools.getBlockFromStack(stack))));
        }

    },
    MINECART {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack) && (stack.getItem() instanceof ItemMinecart || stack.getItem() instanceof IMinecartItem);
        }

    },
    BALLAST {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack) && BallastRegistry.isItemBallast(stack);
        }

    },
    EMPTY_BUCKET {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            if (InvTools.isEmpty(stack))
                return false;
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                return true;
            if (InvTools.isItem(stack, Items.BUCKET))
                return true;
            UniversalBucket uBucket = ForgeModContainer.getInstance().universalBucket;
            FluidStack fluidStack;
            return uBucket != null && InvTools.extendsItemClass(stack, UniversalBucket.class) && (fluidStack = uBucket.getFluid(stack)) != null && fluidStack.amount <= 0;
        }

    },
    FEED {
        @Override
        @Contract("null->false")
        public boolean test(@Nullable ItemStack stack) {
            return !InvTools.isEmpty(stack) && (stack.getItem() instanceof ItemFood || stack.getItem() == Items.WHEAT || stack.getItem() instanceof ItemSeeds);
        }

    };

    @Override
    @Contract("null->false")
    public abstract boolean test(@Nullable ItemStack stack);

}
