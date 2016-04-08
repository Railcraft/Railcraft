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

@RailcraftModule("tracks")
public class ModuleTracks extends RailcraftModulePayload {

    ModuleTracks() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                RailcraftBlocks.registerBlockTrack();

                if (RailcraftBlocks.getBlockTrack() != null) {
                    MiscTools.registerTrack(EnumTrack.LOCKING);
                    MiscTools.registerTrack(EnumTrack.ONEWAY);
                    MiscTools.registerTrack(EnumTrack.CONTROL);
                    MiscTools.registerTrack(EnumTrack.JUNCTION);
                    MiscTools.registerTrack(EnumTrack.SWITCH);
                    MiscTools.registerTrack(EnumTrack.WYE);
                    MiscTools.registerTrack(EnumTrack.DISEMBARK);
                    MiscTools.registerTrack(EnumTrack.EMBARKING);
                    MiscTools.registerTrack(EnumTrack.BUFFER_STOP);
                    MiscTools.registerTrack(EnumTrack.GATED);
                    MiscTools.registerTrack(EnumTrack.GATED_ONEWAY);
                    MiscTools.registerTrack(EnumTrack.DISPOSAL);
                    MiscTools.registerTrack(EnumTrack.DETECTOR_DIRECTION);

//            Block.blocksList[Block.fence.blockID] = null;
//            Block fence = new BlockFenceReplacement(85);
                }
            }
        });
    }

}
