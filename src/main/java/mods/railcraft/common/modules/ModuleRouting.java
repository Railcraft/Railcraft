/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.tracks.kit.TrackKits;
import mods.railcraft.common.blocks.wayobjects.EnumWayObject;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RoutingTableCopyRecipe;
import mods.railcraft.common.util.crafting.RoutingTicketCopyRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:routing", dependencyClasses = {ModuleSignals.class})
public class ModuleRouting extends RailcraftModulePayload {
    public ModuleRouting() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.routingTable,
                        RailcraftItems.ticket,
                        RailcraftItems.ticketGold,
                        RailcraftBlocks.detector
//                        RailcraftBlocks.track
//                        RailcraftBlocks.signal
                );
            }

            @Override
            public void preInit() {
                TrackKits.ROUTING.register();

                if (RailcraftItems.routingTable.isEnabled())
                    CraftingPlugin.addRecipe(new RoutingTableCopyRecipe());

                if (RailcraftItems.ticket.isEnabled() && RailcraftItems.ticketGold.isEnabled())
                    CraftingPlugin.addRecipe(new RoutingTicketCopyRecipe());

                if (EnumDetector.ROUTING.isEnabled()) {
                    CraftingPlugin.addRecipe(EnumDetector.ROUTING.getItem(),
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', new ItemStack(Blocks.QUARTZ_BLOCK, 1, 1),
                            'P', Blocks.STONE_PRESSURE_PLATE);

                }

                if (RailcraftBlocks.wayObject.isEnabled()) {
                    // Define Switch Motor
                    if (EnumWayObject.SWITCH_ROUTING.isEnabled() && EnumWayObject.SWITCH_MOTOR.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(EnumWayObject.SWITCH_ROUTING.getItem(), EnumWayObject.SWITCH_MOTOR.getItem(), EnumDetector.ROUTING.getItem());
                    }
                }
            }
        });
    }

}
