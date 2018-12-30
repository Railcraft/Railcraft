/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.function.Predicate;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements Predicate<ItemStack> {

    ALL,
    FUEL {
        @Override
        protected boolean testType(ItemStack stack) {
            return FuelPlugin.getBurnTime(stack) > 0;
        }

    },
    TRACK {
        @Override
        protected boolean testType(ItemStack stack) {
            return stack.getItem() instanceof ITrackItem || (stack.getItem() instanceof ItemBlock && TrackTools.isRailBlock(InvTools.getBlockStateFromStack(stack)));
        }

    },
    MINECART {
        @Override
        protected boolean testType(ItemStack stack) {
            return stack.getItem() instanceof ItemMinecart || stack.getItem() instanceof IMinecartItem;
        }

    },
    BALLAST {
        @Override
        protected boolean testType(ItemStack stack) {
            return BallastRegistry.isItemBallast(stack);
        }

    },
    //    EMPTY_BUCKET {
//        @Override
//        protected boolean testType(ItemStack stack) {
//            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
//                return true;
//            if (InvTools.isItem(stack, Items.BUCKET))
//                return true;
//            UniversalBucket uBucket = ForgeModContainer.getInstance().universalBucket;
//            FluidStack fluidStack;
//            return uBucket != null && StackFilters.of(UniversalBucket.class).test(stack) && (fluidStack = uBucket.getFluid(stack)) != null && fluidStack.amount <= 0;
//        }
//
//    },
    FLUID_CONTAINER {
        @Override
        protected boolean testType(ItemStack stack) {
            return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        }
    },
    FEED {
        @Override
        protected boolean testType(ItemStack stack) {
            return stack.getItem() instanceof ItemFood || stack.getItem() == Items.WHEAT || stack.getItem() instanceof ItemSeeds;
        }

    };

    protected boolean testType(ItemStack stack) {
        return true;
    }

    @Override
    public boolean test(ItemStack stack) {
        return !InvTools.isEmpty(stack) && testType(stack);
    }
}
