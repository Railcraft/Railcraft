/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.filters;

import net.minecraft.item.*;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.init.Items;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StackFilter implements IStackFilter {

    ALL {
        @Override
        public boolean matches(ItemStack stack) {
            return true;
        }

    },
    FUEL {
        @Override
        public boolean matches(ItemStack stack) {
            return FuelPlugin.getBurnTime(stack) > 0;
        }

    },
    TRACK {
        @Override
        public boolean matches(ItemStack stack) {
            return stack.getItem() instanceof ITrackItem || (stack.getItem() instanceof ItemBlock && TrackTools.isRailBlock(((ItemBlock)stack.getItem()).field_150939_a));
        }

    },
    MINECART {
        @Override
        public boolean matches(ItemStack stack) {
            return stack.getItem() instanceof ItemMinecart || stack.getItem() instanceof IMinecartItem;
        }

    },
    BALLAST {
        @Override
        public boolean matches(ItemStack stack) {
            return BallastRegistry.isItemBallast(stack);
        }

    },
    EMPTY_BUCKET {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() == Items.bucket;
        }

    },
    FEED {
        @Override
        public boolean matches(ItemStack stack) {
            return stack.getItem() instanceof ItemFood || stack.getItem() == Items.wheat || stack.getItem() instanceof ItemSeeds;
        }

    };

    public static void initialize() {
        for (StackFilter type : StackFilter.values()) {
            IStackFilter.filters.put(type.name(), type);
        }
    }

    @Override
    public abstract boolean matches(ItemStack stack);

}
