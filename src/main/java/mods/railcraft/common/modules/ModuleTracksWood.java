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

@RailcraftModule("railcraft:tracks|wood")
public class ModuleTracksWood extends RailcraftModulePayload {

    public ModuleTracksWood() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
//                        RailcraftBlocks.track,
                        RailcraftBlocks.TRACK_STRAP_IRON
                );
            }

            @Override
            public void preInit() {
//                TrackKits.SLOW.register();
//                TrackKits.BOOSTER.register();
//                TrackKits.SLOW_JUNCTION.register();
//                TrackKits.SLOW_SWITCH.register();
//                TrackKits.SLOW_WYE.register();
            }
        });
    }
}
