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
import net.minecraftforge.common.config.Configuration;

@RailcraftModule(value = "railcraft:tracks|strap_iron", description = "strap iron tracks")
public class ModuleTracksStrapIron extends RailcraftModulePayload {
    public static Config config;

    public ModuleTracksStrapIron() {
        add(
                RailcraftBlocks.TRACK_FLEX_STRAP_IRON
        );
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleTracksStrapIron.config = new Config(config);
    }

    public static class Config {
        public final float maxSpeed;

        public Config(Configuration config) {
            maxSpeed = config.getFloat("maxSpeed", CAT_CONFIG, 0.12f, 0.1f, 0.3f,
                    "change to limit max speed on strap iron rails\n" +
                            "iron tracks operate at 0.4 blocks per tick");
        }
    }
}
