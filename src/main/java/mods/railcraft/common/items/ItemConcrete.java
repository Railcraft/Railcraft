/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class ItemConcrete extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(2),
                "GQ",
                "QG",
                'Q', Items.QUARTZ,
                'G', Blocks.GRAVEL);
        CraftingPlugin.addShapedRecipe(getStack(2),
                "GS",
                "QG",
                'Q', Items.QUARTZ,
                'S', RailcraftItems.DUST, ItemDust.EnumDust.SLAG,
                'G', Blocks.GRAVEL);
        CraftingPlugin.addShapedRecipe(getStack(2),
                "GQ",
                "SG",
                'Q', Items.QUARTZ,
                'S', RailcraftItems.DUST, ItemDust.EnumDust.SLAG,
                'G', Blocks.GRAVEL);

    }

}
