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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.charge.ChargeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:charge", description = "all things charge")
public class ModuleCharge extends RailcraftModulePayload {
    public static Config config;

    public ModuleCharge() {
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

        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                MinecraftForge.EVENT_BUS.register(ChargeManager.DISTRIBUTION);
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleCharge.config = new Config(config);
    }

    public static class Config {
        public final float lossMultiplier;
        public final boolean debug;

        public Config(Configuration config) {
            lossMultiplier = config.getFloat("lossMultiplier", CAT_CONFIG,
                    1.0F, 0.2F, 10F,
                    "adjust the losses for the Charge network");
            debug = config.getBoolean("printDebug", CAT_CONFIG, false, "change to 'true' to enabled Charge Network debug spam");
        }
    }
}
