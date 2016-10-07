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
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:train", softDependencyClasses = ModuleTracks.class, description = "cart linking, train dispenser, coupler track kit")
public class ModuleTrain extends RailcraftModulePayload {

    public ModuleTrain() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.MANIPULATOR,
                        TrackKits.COUPLER
//                        RailcraftBlocks.track
                );
            }

            @Override
            public void preInit() {
                if (RailcraftBlocks.DETECTOR.isLoaded()) {
                    CraftingPlugin.addRecipe(EnumDetector.TRAIN.getItem(),
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', Blocks.NETHER_BRICK,
                            'P', Blocks.STONE_PRESSURE_PLATE);
                }
            }

            @Override
            public void init() {
                ManipulatorVariant type = ManipulatorVariant.DISPENSER_TRAIN;
                if (type.isAvailable() && ManipulatorVariant.DISPENSER_CART.isAvailable()) {
                    CraftingPlugin.addRecipe(type.getItem(),
                            "rcr",
                            "cdc",
                            "rcr",
                            'd', ManipulatorVariant.DISPENSER_CART.getItem(),
                            'c', IToolCrowbar.ORE_TAG,
                            'r', "dustRedstone");
                }
            }
        });
    }
}
