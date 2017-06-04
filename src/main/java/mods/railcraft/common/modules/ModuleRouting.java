/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.common.blocks.machine.wayobjects.actuators.ActuatorVariant;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RoutingTableCopyRecipe;
import mods.railcraft.common.util.crafting.RoutingTicketCopyRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:routing", dependencyClasses = {ModuleSignals.class}, softDependencyClasses = ModuleTracks.class, description = "routing tables, tickets, detectors, etc...")
public class ModuleRouting extends RailcraftModulePayload {
    public ModuleRouting() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.ROUTING_TABLE,
                        RailcraftItems.TICKET,
                        RailcraftItems.TICKET_GOLD,
                        RailcraftBlocks.DETECTOR,
                        TrackKits.ROUTING
//                        RailcraftBlocks.track
//                        RailcraftBlocks.signal
                );
            }

            @Override
            public void init() {
                if (RailcraftItems.ROUTING_TABLE.isEnabled())
                    CraftingPlugin.addRecipe(new RoutingTableCopyRecipe());

                if (RailcraftItems.TICKET.isEnabled() && RailcraftItems.TICKET_GOLD.isEnabled())
                    CraftingPlugin.addRecipe(new RoutingTicketCopyRecipe());

                if (EnumDetector.ROUTING.isEnabled()) {
                    CraftingPlugin.addRecipe(EnumDetector.ROUTING.getStack(),
                        "XXX",
                        "XPX",
                        "XXX",
                        'X', new ItemStack(Blocks.QUARTZ_BLOCK, 1, 1),
                        'P', Blocks.STONE_PRESSURE_PLATE);

                }

                if (RailcraftBlocks.ACTUATOR.isEnabled()) {
                    // Define Switch Motor
                    if (ActuatorVariant.ROUTING.isEnabled() && ActuatorVariant.MOTOR.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(ActuatorVariant.ROUTING.getStack(), ActuatorVariant.MOTOR.getStack(), EnumDetector.ROUTING.getStack());
                    }

                    // Lever -> Motor upgrade recipe
                    if (ActuatorVariant.LEVER.isEnabled() && ActuatorVariant.MOTOR.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(ActuatorVariant.MOTOR.getStack(), ActuatorVariant.LEVER.getStack(), RailcraftItems.CIRCUIT.getRecipeObject(
                            ItemCircuit.EnumCircuit.RECEIVER));
                    }
                }
            }
        });
    }

}
