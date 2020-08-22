/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.events.CartLinkEvent;
import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:trains", softDependencyClasses = ModuleTracks.class, description = "cart linking, train dispenser, coupler track kit")
public class ModuleTrains extends RailcraftModulePayload {
    public static Config config;

    public ModuleTrains() {
        add(
                RailcraftBlocks.MANIPULATOR,
                TrackKits.COUPLER
        );
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                MinecraftForge.EVENT_BUS.register(new Object() {
                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public void onLinking(CartLinkEvent.Link event) {
                        Train.repairTrain(event.getCartOne(), event.getCartTwo());
                    }

                    @SubscribeEvent(priority = EventPriority.HIGHEST)
                    public void onUnlinking(CartLinkEvent.Unlink event) {
                        Train.killTrain(event.getCartOne());
                        Train.killTrain(event.getCartTwo());
                    }
                });
            }

            @Override
            public void init() {
                if (RailcraftBlocks.DETECTOR.isLoaded()) {
                    CraftingPlugin.addShapedRecipe(EnumDetector.TRAIN.getStack(),
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', Blocks.NETHER_BRICK,
                            'P', Blocks.STONE_PRESSURE_PLATE);
                }
                ManipulatorVariant type = ManipulatorVariant.DISPENSER_TRAIN;
                if (type.isAvailable() && ManipulatorVariant.DISPENSER_CART.isAvailable()) {
                    CraftingPlugin.addShapedRecipe(type.getStack(),
                            "rcr",
                            "cdc",
                            "rcr",
                            'd', ManipulatorVariant.DISPENSER_CART.getStack(),
                            'c', IToolCrowbar.ORE_TAG,
                            'r', "dustRedstone");
                }
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleTrains.config = new Config(config);
    }

    public static class Config {
        public final boolean debug;

        public Config(Configuration config) {
            debug = config.getBoolean("printDebug", CAT_CONFIG, false, "change to 'true' to log debug info for Cart Linking");
        }
    }
}
