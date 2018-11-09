/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.IRollingMachineCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;

public class ItemTrackParts extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        IRollingMachineCraftingManager cm = RailcraftCraftingManager.rollingMachine();
        cm.addShapelessRecipe(getStack(), "ingotBronze");
        cm.addShapelessRecipe(getStack(), "ingotIron");
        cm.addShapelessRecipe(getStack(2), "ingotSteel");
        cm.addShapelessRecipe(getStack(3), "ingotTungsten");
        cm.addShapelessRecipe(getStack(3), "ingotTitanium");
        cm.addShapelessRecipe(getStack(4), "ingotTungstensteel");
    }

}
