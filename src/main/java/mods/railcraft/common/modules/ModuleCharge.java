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
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RotorRepairRecipe;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:charge", description = "all things charge")
public class ModuleCharge extends RailcraftModulePayload {

    public ModuleCharge() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                MinecraftForge.EVENT_BUS.register(ChargeManager.getEventListener());

                add(
                        RailcraftItems.CHARGE_METER,
//                        RailcraftBlocks.track,
                        RailcraftBlocks.CHARGE_FEEDER,
                        RailcraftBlocks.CHARGE_TRAP,
                        RailcraftBlocks.FRAME,
                        RailcraftBlocks.WIRE
//                        RailcraftBlocks.machine_alpha,
//                        RailcraftBlocks.machine_delta,
//                        RailcraftBlocks.machine_epsilon
                );
            }

            @Override
            public void preInit() {
                EnumMachineAlpha alpha = EnumMachineAlpha.TURBINE;
                if (alpha.isAvailable()) {
                    CraftingPlugin.addRecipe(alpha.getItem(3),
                            "BPB",
                            "P P",
                            "BPB",
                            'P', RailcraftItems.PLATE.getRecipeObject(Metal.STEEL),
                            'B', "blockSteel");

                    RailcraftItems.TURBINE_ROTOR.register();

                    CraftingPlugin.addRecipe(new RotorRepairRecipe());

//                ItemStack rotor = RailcraftPartItems.getTurbineRotor();
//                rotor.setItemDamage(25000);
//                CraftingPlugin.addShapelessRecipe(rotor, RailcraftPartItems.getTurbineRotor());
                }

                EnumMachineEpsilon epsilon = EnumMachineEpsilon.FORCE_TRACK_EMITTER;
                if (epsilon.isAvailable()) {
                    CraftingPlugin.addRecipe(epsilon.getItem(),
                            "PCP",
                            "CDC",
                            "PCP",
                            'P', RailcraftItems.PLATE.getRecipeObject(Metal.TIN),
                            'D', "blockDiamond",
                            'C', "ingotCopper");
                }

                epsilon = EnumMachineEpsilon.FLUX_TRANSFORMER;
                if (epsilon.isAvailable())
                    CraftingPlugin.addRecipe(epsilon.getItem(2),
                            "PGP",
                            "GRG",
                            "PGP",
                            'P', RailcraftItems.PLATE.getRecipeObject(Metal.COPPER),
                            'G', "ingotGold",
                            'R', "blockRedstone");
            }
        });
    }
}
