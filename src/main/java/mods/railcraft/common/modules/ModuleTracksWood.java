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
import mods.railcraft.common.util.misc.MiscTools;

@RailcraftModule("tracks|wood")
public class ModuleTracksWood extends RailcraftModulePayload {

    public ModuleTracksWood() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                RailcraftBlocks.registerBlockTrack();

                if (RailcraftBlocks.getBlockTrack() != null) {
                    MiscTools.registerTrack(EnumTrack.SLOW);
                    MiscTools.registerTrack(EnumTrack.SLOW_BOOSTER);
                    MiscTools.registerTrack(EnumTrack.SLOW_JUNCTION);
                    MiscTools.registerTrack(EnumTrack.SLOW_SWITCH);
                    MiscTools.registerTrack(EnumTrack.SLOW_WYE);
                }
            }
        });
    }
}
