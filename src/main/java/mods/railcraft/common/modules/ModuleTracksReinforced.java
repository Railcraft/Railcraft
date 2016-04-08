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

@RailcraftModule("tracks|reinforced")
public class ModuleTracksReinforced extends RailcraftModulePayload {

    public ModuleTracksReinforced() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                RailcraftBlocks.registerBlockTrack();

                if (RailcraftBlocks.getBlockTrack() != null) {
                    MiscTools.registerTrack(EnumTrack.REINFORCED);
                    MiscTools.registerTrack(EnumTrack.REINFORCED_BOOSTER);
                    MiscTools.registerTrack(EnumTrack.REINFORCED_JUNCTION);
                    MiscTools.registerTrack(EnumTrack.REINFORCED_SWITCH);
                    MiscTools.registerTrack(EnumTrack.REINFORCED_WYE);
                }
            }
        });
    }
}
