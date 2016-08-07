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

@RailcraftModule("railcraft:tracks")
public class ModuleTracks extends RailcraftModulePayload {

    public ModuleTracks() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
//                        RailcraftBlocks.track
                );
            }

            @Override
            public void preInit() {
//                TrackKits.LOCKING.register();
//                TrackKits.ONEWAY.register();
//                TrackKits.CONTROL.register();
//                TrackKits.JUNCTION.register();
//                TrackKits.SWITCH.register();
//                TrackKits.WYE.register();
//                TrackKits.DISEMBARK.register();
//                TrackKits.EMBARKING.register();
//                TrackKits.BUFFER_STOP.register();
//                TrackKits.GATED.register();
//                TrackKits.GATED_ONEWAY.register();
//                TrackKits.DISPOSAL.register();
//                TrackKits.DETECTOR_DIRECTION.register();
            }
        });
    }

}
