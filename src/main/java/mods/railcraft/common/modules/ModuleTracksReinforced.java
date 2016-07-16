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

@RailcraftModule("railcraft:tracks|reinforced")
public class ModuleTracksReinforced extends RailcraftModulePayload {

    public ModuleTracksReinforced() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(RailcraftBlocks.track);
            }

            @Override
            public void preInit() {
                EnumTrack.REINFORCED.register();
                EnumTrack.REINFORCED_BOOSTER.register();
                EnumTrack.REINFORCED_JUNCTION.register();
                EnumTrack.REINFORCED_SWITCH.register();
                EnumTrack.REINFORCED_WYE.register();
            }
        });
    }
}
