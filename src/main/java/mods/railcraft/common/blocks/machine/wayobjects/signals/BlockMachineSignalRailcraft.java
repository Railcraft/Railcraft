/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;

/**
 * Created by CovertJaguar on 7/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = SignalVariant.class)
public class BlockMachineSignalRailcraft extends BlockMachineSignal<SignalVariant> {
    @Override
    public void defineRecipes() {
        SignalVariant.BLOCK.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LCI",
                " BI",
                "   ",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalVariant.DISTANT.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LCI",
                " BI",
                "   ",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalVariant.DUAL_HEAD_BLOCK.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LCI",
                " BI",
                "LRI",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalVariant.DUAL_HEAD_DISTANT.ifAvailable(v -> CraftingPlugin.addRecipe(v.getStack(),
                "LRI",
                " BI",
                "LRI",
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));
    }
}
