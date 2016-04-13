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
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("tracks|wood")
public class ModuleTrain extends RailcraftModulePayload {

    public ModuleTrain() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.detector,
                        RailcraftBlocks.track
                );
            }

            @Override
            public void preInit() {
                EnumTrack.COUPLER.register();

                if (RailcraftBlocks.detector.isLoaded()) {
                    CraftingPlugin.addRecipe(EnumDetector.TRAIN.getItem(),
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', Blocks.nether_brick,
                            'P', Blocks.stone_pressure_plate);
                }

                EnumMachineGamma.DISPENSER_TRAIN.register();
            }

            @Override
            public void init() {
                EnumMachineGamma type = EnumMachineGamma.DISPENSER_TRAIN;
                if (type.isAvailable() && EnumMachineGamma.DISPENSER_CART.isAvailable()) {
                    CraftingPlugin.addRecipe(type.getItem(),
                            "rcr",
                            "cdc",
                            "rcr",
                            'd', EnumMachineGamma.DISPENSER_CART.getItem(),
                            'c', IToolCrowbar.ORE_TAG,
                            'r', "dustRedstone");
                }
            }
        });
    }
}
