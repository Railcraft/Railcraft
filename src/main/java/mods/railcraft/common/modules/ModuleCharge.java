/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.charge.ChargeManager;
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
                MinecraftForge.EVENT_BUS.register(ChargeManager.DISTRIBUTION);

                add(
                        RailcraftBlocks.BATTERY_NICKEL_IRON,
                        RailcraftBlocks.BATTERY_NICKEL_ZINC,
                        RailcraftBlocks.BATTERY_ZINC_CARBON,
                        RailcraftBlocks.BATTERY_ZINC_SILVER,
                        RailcraftBlocks.CHARGE_FEEDER,
                        RailcraftBlocks.CHARGE_TRAP,
                        RailcraftBlocks.FRAME,
                        RailcraftBlocks.STEAM_TURBINE,
                        RailcraftBlocks.WIRE,
                        RailcraftItems.CHARGE,
                        RailcraftItems.CHARGE_METER,
                        RailcraftItems.TURBINE_BLADE,
                        RailcraftItems.TURBINE_DISK,
                        RailcraftItems.TURBINE_ROTOR
                );
            }

            @Override
            public void init() {
//                EnumMachineAlpha alpha = EnumMachineAlpha.TURBINE;
//                if (alpha.isAvailable()) {
//                    CraftingPlugin.addRecipe(alpha.getStack(3),
//                            "BPB",
//                            "P P",
//                            "BPB",
//                            'P', RailcraftItems.PLATE, Metal.STEEL,
//                            'B', "blockSteel");


//                }

//                EnumMachineEpsilon epsilon = EnumMachineEpsilon.FORCE_TRACK_EMITTER;
//                if (epsilon.isAvailable()) {
//                    CraftingPlugin.addRecipe(epsilon.getStack(),
//                            "PCP",
//                            "CDC",
//                            "PCP",
//                            'P', RailcraftItems.PLATE.getRecipeObject(Metal.TIN),
//                            'D', "blockDiamond",
//                            'C', "ingotCopper");
//                }

//                epsilon = EnumMachineEpsilon.FLUX_TRANSFORMER;
//                if (epsilon.isAvailable())
//                    CraftingPlugin.addRecipe(epsilon.getStack(2),
//                            "PGP",
//                            "GRG",
//                            "PGP",
//                            'P', RailcraftItems.PLATE.getRecipeObject(Metal.COPPER),
//                            'G', "ingotGold",
//                            'R', "blockRedstone");
            }
        });
    }
}
