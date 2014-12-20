/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import net.minecraft.item.ItemStack;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;

public class ItemWhistleTuner extends ItemRailcraft {

    private static ItemWhistleTuner item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.whistle.tuner";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemWhistleTuner();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true,
                        "N N",
                        "NNN",
                        " N ",
                        'N', "nuggetSteel");
            }
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public ItemWhistleTuner() {
        setMaxDamage(250);
    }

}
