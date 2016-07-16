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
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RotorRepairRecipe;
import net.minecraft.init.Items;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule("railcraft:electricity")
public class ModuleElectricity extends RailcraftModulePayload {

    public ModuleElectricity() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.electricMeter,
                        RailcraftBlocks.track,
                        RailcraftBlocks.frame,
                        RailcraftBlocks.machine_alpha,
                        RailcraftBlocks.machine_delta,
                        RailcraftBlocks.machine_epsilon
                );
            }

            @Override
            public void preInit() {
                EnumMachineAlpha alpha = EnumMachineAlpha.TURBINE;
                if (alpha.isEnabled()) {
                    CraftingPlugin.addRecipe(alpha.getItem(3),
                            "BPB",
                            "P P",
                            "BPB",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.STEEL),
                            'B', "blockSteel");

                    RailcraftItems.turbineRotor.register();

                    CraftingPlugin.addRecipe(new RotorRepairRecipe());

//                ItemStack rotor = RailcraftPartItems.getTurbineRotor();
//                rotor.setItemDamage(25000);
//                CraftingPlugin.addShapelessRecipe(rotor, RailcraftPartItems.getTurbineRotor());
                }

                EnumMachineEpsilon epsilon = EnumMachineEpsilon.ELECTRIC_FEEDER;
                if (epsilon.isEnabled())
                    CraftingPlugin.addRecipe(epsilon.getItem(),
                            "PCP",
                            "CCC",
                            "PCP",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.TIN),
                            'C', "ingotCopper");

                epsilon = EnumMachineEpsilon.FORCE_TRACK_EMITTER;
                if (epsilon.isEnabled()) {
                    EnumTrack.FORCE.register();
                    CraftingPlugin.addRecipe(epsilon.getItem(),
                            "PCP",
                            "CDC",
                            "PCP",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.TIN),
                            'D', "blockDiamond",
                            'C', "ingotCopper");
                }

                epsilon = EnumMachineEpsilon.FLUX_TRANSFORMER;
                if (epsilon.isEnabled())
                    CraftingPlugin.addRecipe(epsilon.getItem(2),
                            "PGP",
                            "GRG",
                            "PGP",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.COPPER),
                            'G', "ingotGold",
                            'R', "blockRedstone");

                EnumMachineDelta delta = EnumMachineDelta.WIRE;
                if (delta.isEnabled()) {
                    RailcraftCraftingManager.rollingMachine.addRecipe(
                            delta.getItem(8),
                            "LPL",
                            "PCP",
                            "LPL",
                            'C', "blockCopper",
                            'P', Items.PAPER,
                            'L', "ingotLead");
                }

            }
        });
    }
}
