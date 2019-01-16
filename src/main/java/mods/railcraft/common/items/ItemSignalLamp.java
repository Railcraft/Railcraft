/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;

public class ItemSignalLamp extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "PG ",
                "PYT",
                "PRS",
                'G', EnumColor.LIME.getDyeOreDictTag(),
                'Y', EnumColor.YELLOW.getDyeOreDictTag(),
                'R', EnumColor.RED.getDyeOreDictTag(),
                'S', "dustRedstone",
                'T', "dustGlowstone",
                'P', "paneGlassColorless");
    }

}
