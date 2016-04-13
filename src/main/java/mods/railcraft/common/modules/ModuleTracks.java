/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.EnumTrack;

@RailcraftModule("tracks")
public class ModuleTracks extends RailcraftModulePayload {

    public ModuleTracks() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(RailcraftBlocks.track);
            }

            @Override
            public void preInit() {
                EnumTrack.LOCKING.register();
                EnumTrack.ONEWAY.register();
                EnumTrack.CONTROL.register();
                EnumTrack.JUNCTION.register();
                EnumTrack.SWITCH.register();
                EnumTrack.WYE.register();
                EnumTrack.DISEMBARK.register();
                EnumTrack.EMBARKING.register();
                EnumTrack.BUFFER_STOP.register();
                EnumTrack.GATED.register();
                EnumTrack.GATED_ONEWAY.register();
                EnumTrack.DISPOSAL.register();
                EnumTrack.DETECTOR_DIRECTION.register();
            }
        });
    }

}
