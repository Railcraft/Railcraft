/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemSignalLamp extends ItemRailcraft {

    public ItemSignalLamp() {
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "PG ",
                "PYT",
                "PRS",
                'G', EnumColor.LIME.getDye(),
                'Y', EnumColor.YELLOW.getDye(),
                'R', EnumColor.RED.getDye(),
                'S', "dustRedstone",
                'T', "dustGlowstone",
                'P', "paneGlassColorless");
    }

}
