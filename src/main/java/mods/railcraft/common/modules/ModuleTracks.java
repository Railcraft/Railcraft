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
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import mods.railcraft.common.items.RailcraftItems;

@RailcraftModule("railcraft:tracks")
public class ModuleTracks extends RailcraftModulePayload {

    public ModuleTracks() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.TRACK_KIT,
                        RailcraftBlocks.TRACK_OUTFITTED,
                        TrackKits.ACTIVATOR,
                        TrackKits.BOOSTER,
                        TrackKits.BUFFER_STOP,
                        TrackKits.CONTROL,
                        TrackKits.DETECTOR,
                        TrackKits.DETECTOR_TRAVEL,
                        TrackKits.DISEMBARK,
                        TrackKits.DUMPING,
                        TrackKits.EMBARKING,
                        TrackKits.GATED,
                        TrackKits.GATED_ONE_WAY,
                        TrackKits.LOCKING,
                        TrackKits.ONE_WAY
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
