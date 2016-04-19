/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@Deprecated
public class RailcraftBlocksOld {

    private static Block blockRailElevator;

    public static void registerBlockRailElevator() {
        if (blockRailElevator == null && RailcraftConfig.isBlockEnabled("elevator")) {
            blockRailElevator = new BlockTrackElevator().setUnlocalizedName("railcraft.track.elevator");
            RailcraftRegistry.register(blockRailElevator, ItemBlockRailcraft.class);
            blockRailElevator.setHarvestLevel("crowbar", 0);
            ItemStack stackElevator = new ItemStack(blockRailElevator, 8);
            CraftingPlugin.addRecipe(stackElevator,
                    "IRI",
                    "ISI",
                    "IRI",
                    'I', RailcraftConfig.useOldRecipes() ? "ingotGold" : RailcraftItems.rail.getRecipeObject(EnumRail.ADVANCED),
                    'S', RailcraftConfig.useOldRecipes() ? "ingotIron" : RailcraftItems.rail.getRecipeObject(EnumRail.STANDARD),
                    'R', "dustRedstone");
        }
    }

    public static Block getBlockElevator() {
        return blockRailElevator;
    }

}
