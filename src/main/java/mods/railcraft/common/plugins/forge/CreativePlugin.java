/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.items.ItemCrowbarReinforced;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
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
            return ItemCrowbarReinforced.getItem();
        }

        @Override
        public String getTranslatedTabLabel() {
            return "Railcraft";
        }

        @Override
        public Item getTabIconItem() {
            return ItemCrowbarReinforced.item;
        }

    }
}
