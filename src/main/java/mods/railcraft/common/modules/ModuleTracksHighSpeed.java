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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;

@RailcraftModule(value = "railcraft:tracks|high_speed", description = "high speed tracks")
public class ModuleTracksHighSpeed extends RailcraftModulePayload {

    public ModuleTracksHighSpeed() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.TRACK_FLEX_HIGH_SPEED,
                        TrackKits.HIGH_SPEED_TRANSITION
                );
            }
        });
    }
}
