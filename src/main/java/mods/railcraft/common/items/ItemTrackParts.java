/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
        IRollingMachineCraftingManager cm = RailcraftCraftingManager.rollingMachine;
        cm.addShapelessRecipe(getStack(3), "ingotBronze");
        cm.addShapelessRecipe(getStack(4), "ingotIron");
        cm.addShapelessRecipe(getStack(8), "ingotSteel");
    }

}
