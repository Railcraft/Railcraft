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
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.items.RailcraftItems;

@RailcraftModule(value = "railcraft:tracks", description = "track kits, outfitted track")
public class ModuleTracks extends RailcraftModulePayload {

    public ModuleTracks() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.TRACK_KIT,
                        RailcraftItems.SPIKE_MAUL_IRON,
                        RailcraftItems.SPIKE_MAUL_STEEL,
                        RailcraftBlocks.MACHINE_ACTUATOR,
                        RailcraftBlocks.TRACK_OUTFITTED,
//                        RailcraftBlocks.TRACK_JUNCTION_IRON,
                        TrackKits.ACTIVATOR,
                        TrackKits.BOOSTER,
                        TrackKits.BUFFER_STOP,
                        TrackKits.CONTROL,
                        TrackKits.DETECTOR,
                        TrackKits.DISEMBARK,
                        TrackKits.DUMPING,
                        TrackKits.EMBARKING,
                        TrackKits.GATED,
                        TrackKits.LOCKING,
                        TrackKits.ONE_WAY,
                        TrackKits.JUNCTION
                );
            }

            @Override
            public void preInit() {
//                TrackKits.JUNCTION.register();
//                TrackKits.SWITCH.register();
//                TrackKits.WYE.register();
            }
        });
    }

}
