package mods.railcraft.api.crafting;

import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.CokeOvenCraftingManager;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;

/**
 *
 */
public final class CraftingApiAccess {

    public static void initialize() {
        RailcraftCraftingManager.cokeOven = CokeOvenCraftingManager.getInstance();
        RailcraftCraftingManager.blastFurnace = BlastFurnaceCraftingManager.getInstance();
        RailcraftCraftingManager.rockCrusher = RockCrusherCraftingManager.getInstance();
        RailcraftCraftingManager.rollingMachine = RollingMachineCraftingManager.getInstance();
    }

    private CraftingApiAccess() {
    }
}
