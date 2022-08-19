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
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.dynamiclights.DynamicLightsPlugin;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.ToIntFunction;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:locomotives", softDependencyClasses = ModuleTracks.class, description = "locomotives, locomotive related tracks, train drag")
public class ModuleLocomotives extends RailcraftModulePayload {
    public static Config config;

    public ModuleLocomotives() {
        add(
                RailcraftItems.WHISTLE_TUNER,
                TrackKits.WHISTLE,
                TrackKits.LOCOMOTIVE,
                TrackKits.THROTTLE,
                RailcraftCarts.LOCO_STEAM_SOLID,
                RailcraftCarts.LOCO_DIESEL,
                RailcraftCarts.LOCO_ELECTRIC,
                RailcraftCarts.LOCO_CREATIVE
        );

        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void postInit() {
                if (FMLCommonHandler.instance().getSide().isClient()) {
                    int lightLevel = RailcraftConfig.locomotiveLightLevel();
                    if (lightLevel >= 0) {
                        registerDynamicLights(lightLevel);
                    }
                }
            }

            private void registerDynamicLights(int lightLevel) {
                DynamicLightsPlugin plugin = DynamicLightsPlugin.getInstance();
                ToIntFunction<Entity> lightCalculator = entity -> (entity == null || entity.isDead
                        || !(entity instanceof EntityLocomotive) || ((EntityLocomotive) entity).isShutdown()) ? 0 : lightLevel;
                plugin.registerEntityLightSource(EntityLocomotiveSteamSolid.class, lightCalculator);
                plugin.registerEntityLightSource(EntityLocomotiveDiesel.class, lightCalculator);
                plugin.registerEntityLightSource(EntityLocomotiveElectric.class, lightCalculator);
                plugin.registerEntityLightSource(EntityLocomotiveCreative.class, lightCalculator);
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleLocomotives.config = new Config(config);
    }

    public static class Config {
        public final float steamLocomotiveEfficiency;
        public final boolean locomotiveDamageMobs;
        public final int locomotiveHorsepower;

        public Config(Configuration config) {
            steamLocomotiveEfficiency = config.getFloat("steamLocomotiveEfficiency", CAT_CONFIG,
                    3.0F, 0.2F, 12F,
                    "adjust the multiplier used when calculating fuel use");

            locomotiveDamageMobs = config.getBoolean("damageMobs", CAT_CONFIG, true,
                    "change to 'false' to disable Locomotive damage on mobs, they will still knockback mobs");
            locomotiveHorsepower = config.getInt("horsepower", CAT_CONFIG, 15, 15, 45,
                    "controls how much power locomotives have and how many carts they can pull\n"
                            + "be warned, longer trains have a greater chance for glitches\n"
                            + "as such it HIGHLY recommended you do not change this");
        }
    }
}
