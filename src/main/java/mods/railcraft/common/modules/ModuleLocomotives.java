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
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.dynamiclights.DynamicLightsPlugin;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.ToIntFunction;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:locomotives", softDependencyClasses = ModuleTracks.class, description = "locomotives, locomotive related tracks, train drag")
public class ModuleLocomotives extends RailcraftModulePayload {
    public ModuleLocomotives() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.WHISTLE_TUNER,
                        TrackKits.WHISTLE,
                        TrackKits.LOCOMOTIVE,
                        TrackKits.THROTTLE,
                        RailcraftCarts.LOCO_STEAM_SOLID,
                        RailcraftCarts.LOCO_ELECTRIC,
                        RailcraftCarts.LOCO_CREATIVE
                );
            }

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
                plugin.registerEntityLightSource(EntityLocomotiveElectric.class, lightCalculator);
                plugin.registerEntityLightSource(EntityLocomotiveCreative.class, lightCalculator);
            }
        });
    }
}
