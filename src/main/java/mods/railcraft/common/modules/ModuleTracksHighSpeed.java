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

@RailcraftModule("railcraft:tracks|high_speed")
public class ModuleTracksHighSpeed extends RailcraftModulePayload {

    public ModuleTracksHighSpeed() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(RailcraftBlocks.track);
            }

            @Override
            public void preInit() {
                EnumTrack.SPEED.register();
                EnumTrack.SPEED_BOOST.register();
                EnumTrack.SPEED_TRANSITION.register();
                EnumTrack.SPEED_SWITCH.register();
                EnumTrack.SPEED_WYE.register();
            }
        });
    }
}
