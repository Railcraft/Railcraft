/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018

 This work (the API) is licensed under the "MIT" License,
 see LICENSE.md for details.
 -----------------------------------------------------------------------------*/

package mods.railcraft.api.crafting;

import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;

/**
 *
 */
public final class CraftingApiAccess {

    public static void initialize() {
        Crafters.rollingMachine = RollingMachineCraftingManager.getInstance();
    }

    private CraftingApiAccess() {
    }
}
