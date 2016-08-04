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
import mods.railcraft.common.blocks.tracks.EnumTrack;

@RailcraftModule("railcraft:tracks|wood")
public class ModuleTracksWood extends RailcraftModulePayload {

    public ModuleTracksWood() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
//                        RailcraftBlocks.track,
                        RailcraftBlocks.trackStrapIron
                );
            }

            @Override
            public void preInit() {
                EnumTrack.SLOW.register();
                EnumTrack.SLOW_BOOSTER.register();
                EnumTrack.SLOW_JUNCTION.register();
                EnumTrack.SLOW_SWITCH.register();
                EnumTrack.SLOW_WYE.register();
            }
        });
    }
}
