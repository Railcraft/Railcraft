/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Items;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(SignalBoxVariant.class)
public class BlockMachineSignalBoxRailcraft extends BlockMachineSignalBox<SignalBoxVariant> {

    @Override
    public void defineRecipes() {

        // Define Receiver Box
        SignalBoxVariant.RECEIVER.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER));

        // Define Controller Box
        SignalBoxVariant.CONTROLLER.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER));

        // Define Analog Controller Box
        SignalBoxVariant.ANALOG.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        "ICI",
                        "IQI",
                        'I', "ingotIron",
                        'Q', Items.COMPARATOR,
                        'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER));

        // Define Capacitor Box
        SignalBoxVariant.CAPACITOR.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', Items.REPEATER));

        // Define Signal Block Box
        SignalBoxVariant.RELAY.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        " C ",
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL));

        // Define Signal Sequencer Box
        SignalBoxVariant.SEQUENCER.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'C', Items.COMPARATOR));

        // Define Signal Interlock Box
        SignalBoxVariant.INTERLOCK.ifAvailable(box ->
                CraftingPlugin.addShapedRecipe(box.getStack(),
                        " L ",
                        "ICI",
                        "IRI",
                        'I', "ingotIron",
                        'R', "dustRedstone",
                        'L', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                        'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER));
    }
}
