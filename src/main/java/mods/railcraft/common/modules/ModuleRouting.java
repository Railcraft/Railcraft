/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import java.util.EnumSet;
import java.util.Set;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RoutingTableCopyRecipe;
import mods.railcraft.common.util.crafting.RoutingTicketCopyRecipe;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleRouting extends RailcraftModule {

    @Override
    public Set<Module> getDependencies() {
        return EnumSet.of(Module.SIGNALS);
    }

    @Override
    public boolean canModuleLoad() {
        return true;
    }

    @Override
    public void initFirst() {
        BlockDetector.registerBlock();
        ItemRoutingTable.registerItem();
        ItemTicketGold.registerItem();
        ItemTicket.registerItem();

        MiscTools.registerTrack(EnumTrack.ROUTING);

        if (ItemRoutingTable.item != null)
            CraftingPlugin.addRecipe(new RoutingTableCopyRecipe());

        if (ItemTicket.item != null && ItemTicketGold.item != null)
            CraftingPlugin.addRecipe(new RoutingTicketCopyRecipe());

        if (EnumDetector.ROUTING.isEnabled()) {
            CraftingPlugin.addShapedRecipe(EnumDetector.ROUTING.getItem(),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.quartz_block, 1, 1),
                    'P', Blocks.stone_pressure_plate);

            RailcraftBlocks.registerBlockSignal();
            if (RailcraftBlocks.getBlockSignal() != null)
                // Define Switch Motor
                if (EnumSignal.SWITCH_ROUTING.isEnabled() && EnumSignal.SWITCH_MOTOR.isEnabled()) {

                    ItemStack stack = EnumSignal.SWITCH_ROUTING.getItem();
                    CraftingPlugin.addShapelessRecipe(stack, EnumSignal.SWITCH_MOTOR.getItem(), EnumDetector.ROUTING.getItem());
                }
        }
    }

}
