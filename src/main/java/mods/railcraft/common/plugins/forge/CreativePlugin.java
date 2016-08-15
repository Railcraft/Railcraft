/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CreativePlugin {

    public static final CreativeTabs RAILCRAFT_TAB = new RailcraftTab("railcraft");

    private static class RailcraftTab extends CreativeTabs {

        public RailcraftTab(String label) {
            super(label);
        }

        @Override
        public ItemStack getIconItemStack() {
            return RailcraftItems.CROWBAR_STEEL.getStack();
        }

        @Override
        public String getTranslatedTabLabel() {
            return Railcraft.NAME;
        }

        @Override
        public Item getTabIconItem() {
            return RailcraftItems.CROWBAR_STEEL.item();
        }

    }
}
