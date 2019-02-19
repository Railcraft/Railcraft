/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IRollingMachineCrafter;

public class ItemTrackParts extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        IRollingMachineCrafter cm = Crafters.rollingMachine();
        cm.newRecipe(getStack()).shapeless("nuggetBronze", "nuggetBronze", "nuggetBronze");
        cm.newRecipe(getStack()).shapeless("nuggetIron", "nuggetIron");
        cm.newRecipe(getStack()).shapeless("nuggetSteel");
        cm.newRecipe(getStack(2)).shapeless("nuggetTungsten");
        cm.newRecipe(getStack(2)).shapeless("nuggetTitanium");
        cm.newRecipe(getStack(3)).shapeless("nuggetTungstensteel");
    }

}
