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
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.RailcraftCarts;
import net.minecraftforge.common.config.Configuration;

@RailcraftModule(value = "railcraft:extras", softDependencyClasses = {ModuleCarts.class, ModuleTracks.class}, description = "assorted stuff that doesn't really fit anywhere else")
public class ModuleExtras extends RailcraftModulePayload {
    public static Config config;

    public ModuleExtras() {
        add(
                TrackKits.PRIMING,
                TrackKits.LAUNCHER,
                RailcraftCarts.TNT_WOOD,
                RailcraftBlocks.TRACK_ELEVATOR,
                RailcraftBlocks.LOGBOOK
        );
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleExtras.config = new Config(config);
    }

    public static class Config {
        public final int maxLaunchTrackForce;

        public Config(Configuration config) {
            maxLaunchTrackForce = config.getInt("maxLaunchTrackForce", CAT_CONFIG, 30, 5, 50,
                    "change the value to your desired max launch rail force");
        }
    }
}
