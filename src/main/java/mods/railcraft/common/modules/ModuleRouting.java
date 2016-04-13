/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RoutingTableCopyRecipe;
import mods.railcraft.common.util.crafting.RoutingTicketCopyRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "routing", dependencyClasses = {ModuleSignals.class})
public class ModuleRouting extends RailcraftModulePayload {
    public ModuleRouting() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.routingTable,
                        RailcraftItems.ticket,
                        RailcraftItems.ticketGold
                );
            }

            @Override
            public void preInit() {
                BlockDetector.registerBlock();

                EnumTrack.registerTrack(EnumTrack.ROUTING);

                if (ItemRoutingTable.item != null)
                    CraftingPlugin.addRecipe(new RoutingTableCopyRecipe());

                if (RailcraftItems.ticket.isEnabled() && RailcraftItems.ticketGold.isEnabled())
                    CraftingPlugin.addRecipe(new RoutingTicketCopyRecipe());

                if (EnumDetector.ROUTING.isEnabled()) {
                    CraftingPlugin.addRecipe(EnumDetector.ROUTING.getItem(),
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', new ItemStack(Blocks.quartz_block, 1, 1),
                            'P', Blocks.stone_pressure_plate);

                    RailcraftBlocksOld.registerBlockSignal();
                    if (RailcraftBlocksOld.getBlockSignal() != null)
                        // Define Switch Motor
                        if (EnumSignal.SWITCH_ROUTING.isEnabled() && EnumSignal.SWITCH_MOTOR.isEnabled()) {

                            ItemStack stack = EnumSignal.SWITCH_ROUTING.getItem();
                            CraftingPlugin.addShapelessRecipe(stack, EnumSignal.SWITCH_MOTOR.getItem(), EnumDetector.ROUTING.getItem());
                        }
                }
            }
        });
    }

}
