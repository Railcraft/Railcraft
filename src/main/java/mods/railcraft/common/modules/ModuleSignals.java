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
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@RailcraftModule("signals")
public class ModuleSignals extends RailcraftModulePayload {

    public ModuleSignals() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.signalBlockSurveyor,
                        RailcraftItems.signalTuner,
                        RailcraftItems.signalLabel
                );
            }

            @Override
            public void preInit() {
                RailcraftBlocksOld.registerBlockSignal();

                Block blockSignal = RailcraftBlocksOld.getBlockSignal();
                if (blockSignal != null) {
                    // Define Block Signal
                    EnumSignal structure = EnumSignal.BLOCK_SIGNAL;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {

                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "LCI",
                                " BI",
                                "   ",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL),
                                'I', "ingotIron",
                                'L', RailcraftItems.signalLamp.getRecipeObject(),
                                'B', "dyeBlack");
                    }

                    // Define Dual Head Block Signal
                    structure = EnumSignal.DUAL_HEAD_BLOCK_SIGNAL;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {

                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "LCI",
                                " BI",
                                "LRI",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL),
                                'R', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'I', "ingotIron",
                                'L', RailcraftItems.signalLamp.getRecipeObject(),
                                'B', "dyeBlack");
                    }

                    // Define Distant Signal
                    structure = EnumSignal.DISTANT_SIGNAL;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "LCI",
                                " BI",
                                "   ",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'I', "ingotIron",
                                'L', RailcraftItems.signalLamp.getRecipeObject(),
                                'B', "dyeBlack");
                    }

                    // Define Dual Head Block Signal
                    structure = EnumSignal.DUAL_HEAD_DISTANT_SIGNAL;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "LRI",
                                " BI",
                                "LRI",
                                'R', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'I', "ingotIron",
                                'L', RailcraftItems.signalLamp.getRecipeObject(),
                                'B', "dyeBlack");
                    }

                    // Define Switch Lever
                    structure = EnumSignal.SWITCH_LEVER;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "RBW",
                                "PLI",
                                'W', "dyeWhite",
                                'I', "ingotIron",
                                'L', Blocks.lever,
                                'P', Blocks.piston,
                                'B', "dyeBlack",
                                'R', "dyeRed");
                        CraftingPlugin.addRecipe(stack,
                                "RBW",
                                "ILP",
                                'W', "dyeWhite",
                                'I', "ingotIron",
                                'L', Blocks.lever,
                                'P', Blocks.piston,
                                'B', "dyeBlack",
                                'R', "dyeRed");
                    }

                    // Define Switch Motor
                    structure = EnumSignal.SWITCH_MOTOR;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "RBW",
                                "PCI",
                                'W', "dyeWhite",
                                'I', "ingotIron",
                                'P', Blocks.piston,
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'B', "dyeBlack",
                                'R', "dyeRed");
                        CraftingPlugin.addRecipe(stack,
                                "RBW",
                                "ICP",
                                'W', "dyeWhite",
                                'I', "ingotIron",
                                'P', Blocks.piston,
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'B', "dyeBlack",
                                'R', "dyeRed");
                    }

                    // Define Receiver Box
                    structure = EnumSignal.BOX_RECEIVER;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER));
                    }

                    // Define Controller Box
                    structure = EnumSignal.BOX_CONTROLLER;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
                    }

                    // Define Analog Controller Box
                    structure = EnumSignal.BOX_ANALOG_CONTROLLER;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "ICI",
                                "IQI",
                                'I', "ingotIron",
                                'Q', Items.comparator,
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
                    }

                    // Define Capacitor Box
                    structure = EnumSignal.BOX_CAPACITOR;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'C', Items.repeater);
                    }

                    // Define Signal Block Box
                    structure = EnumSignal.BOX_BLOCK_RELAY;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                " C ",
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL));
                    }

                    // Define Signal Sequencer Box
                    structure = EnumSignal.BOX_SEQUENCER;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'C', Items.comparator);
                    }
                    // Define Signal Interlock Box
                    structure = EnumSignal.BOX_INTERLOCK;
                    if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                        ItemStack stack = structure.getItem();
                        CraftingPlugin.addRecipe(stack,
                                " L ",
                                "ICI",
                                "IRI",
                                'I', "ingotIron",
                                'R', "dustRedstone",
                                'L', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                                'C', RailcraftItems.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
                    }
                }
            }
        });
    }
}
