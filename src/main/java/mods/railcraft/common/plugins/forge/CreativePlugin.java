/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CreativePlugin {

    public static final CreativeTabs RAILCRAFT_TAB = new RailcraftTab("railcraft.general", RailcraftItems.CROWBAR_STEEL.getStack());
    public static final CreativeTabs TRACK_TAB = new RailcraftTab("railcraft.track", new ItemStack(Blocks.DETECTOR_RAIL));

    private static class RailcraftTab extends CreativeTabs {
        private final ItemStack stack;

        public RailcraftTab(String label, ItemStack stack) {
            super(label);
            this.stack = stack;
        }

        @Override
        public ItemStack getIconItemStack() {
            return stack;
        }

        @Override
        public Item getTabIconItem() {
            return stack.getItem();
        }

    }
}
