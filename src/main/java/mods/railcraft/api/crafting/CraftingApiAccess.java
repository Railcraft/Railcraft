package mods.railcraft.api.crafting;

import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;

/**
 *
 */
public final class CraftingApiAccess {

    public static void initialize() {
        RailcraftCraftingManager.blastFurnace = BlastFurnaceCraftingManager.getInstance();
        RailcraftCraftingManager.rockCrusher = RockCrusherCraftingManager.getInstance();
        RailcraftCraftingManager.rollingMachine = RollingMachineCraftingManager.getInstance();
    }

    public static void setCokeOvenCrafting(ICokeOvenCraftingManager manager) {
        RailcraftCraftingManager.cokeOven = manager;
    }

    private CraftingApiAccess() {
    }
}
