/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.signals.ItemSignalBlockSurveyor;
import mods.railcraft.common.blocks.signals.ItemSignalTuner;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class ModuleSignals extends RailcraftModule {

    @Override
    public void initFirst() {
        RailcraftBlocks.registerBlockSignal();

        Block blockSignal = RailcraftBlocks.getBlockSignal();
        if (blockSignal != null) {

            ItemSignalBlockSurveyor.registerItem();
            ItemSignalTuner.registerItem();
            RailcraftItem.signalLabel.registerItem();

            // Define Block Signal
            EnumSignal structure = EnumSignal.BLOCK_SIGNAL;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {

                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "LCI",
                        " BI",
                        "   ",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL),
                        'I', "ingotIron",
                        'L', RailcraftItem.signalLamp.getRecipeObject(),
                        'B', "dyeBlack");
            }

            // Define Dual Head Block Signal
            structure = EnumSignal.DUAL_HEAD_BLOCK_SIGNAL;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {

                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "LCI",
                        " BI",
                        "LRI",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL),
                        'R', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'I', "ingotIron",
                        'L', RailcraftItem.signalLamp.getRecipeObject(),
                        'B', "dyeBlack");
            }

            // Define Distant Signal
            structure = EnumSignal.DISTANT_SIGNAL;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "LCI",
                        " BI",
                        "   ",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'I', "ingotIron",
                        'L', RailcraftItem.signalLamp.getRecipeObject(),
                        'B', "dyeBlack");
            }

            // Define Dual Head Block Signal
            structure = EnumSignal.DUAL_HEAD_DISTANT_SIGNAL;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "LRI",
                        " BI",
                        "LRI",
                        'R', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'I', "ingotIron",
                        'L', RailcraftItem.signalLamp.getRecipeObject(),
                        'B', "dyeBlack");
            }

            // Define Switch Lever
            structure = EnumSignal.SWITCH_LEVER;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "RBW",
                        "PLI",
                        'W', "dyeWhite",
                        'I', "ingotIron",
                        'L', Blocks.lever,
                        'P', Blocks.piston,
                        'B', "dyeBlack",
                        'R', "dyeRed");
                CraftingPlugin.addShapedRecipe(stack,
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
                CraftingPlugin.addShapedRecipe(stack,
                        "RBW",
                        "PCI",
                        'W', "dyeWhite",
                        'I', "ingotIron",
                        'P', Blocks.piston,
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'B', "dyeBlack",
                        'R', "dyeRed");
                CraftingPlugin.addShapedRecipe(stack,
                        "RBW",
                        "ICP",
                        'W', "dyeWhite",
                        'I', "ingotIron",
                        'P', Blocks.piston,
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'B', "dyeBlack",
                        'R', "dyeRed");
            }

            // Define Receiver Box
            structure = EnumSignal.BOX_RECEIVER;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER));
            }

            // Define Controller Box
            structure = EnumSignal.BOX_CONTROLLER;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
            }
            
            // Define Analog Controller Box
            structure = EnumSignal.BOX_ANALOG_CONTROLLER;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            	ItemStack stack = structure.getItem();
            	CraftingPlugin.addShapedRecipe(stack,
            			"ICI",
            			"IQI",
            			'I', "ingotIron",
            			'Q', Items.comparator,
            			'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
            }

            // Define Capacitor Box
            structure = EnumSignal.BOX_CAPACITOR;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
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
                CraftingPlugin.addShapedRecipe(stack,
                        " C ",
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.SIGNAL));
            }

            // Define Signal Sequencer Box
            structure = EnumSignal.BOX_SEQUENCER;
            if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
                ItemStack stack = structure.getItem();
                CraftingPlugin.addShapedRecipe(stack,
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
                CraftingPlugin.addShapedRecipe(stack,
                        " L ",
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'L', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.RECEIVER),
                        'C', RailcraftItem.circuit.getRecipeObject(ItemCircuit.EnumCircuit.CONTROLLER));
            }
        }
    }

}
