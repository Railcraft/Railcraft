/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneRaw extends ItemFirestoneBase {

    public static Item item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.firestone.raw";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFirestoneRaw().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    public static ItemStack getItem() {
        return new ItemStack(item);
    }

    public ItemFirestoneRaw() {
        setMaxStackSize(1);
    }



}
