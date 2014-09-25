/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.util.misc.MiscTools;

public class ModuleTracksHighSpeed extends RailcraftModule
{

    @Override
    public void initFirst()
    {
        RailcraftBlocks.registerBlockTrack();

        if(RailcraftBlocks.getBlockTrack() != null) {
            MiscTools.registerTrack(EnumTrack.SPEED);
            MiscTools.registerTrack(EnumTrack.SPEED_BOOST);
            MiscTools.registerTrack(EnumTrack.SPEED_TRANSITION);
            MiscTools.registerTrack(EnumTrack.SPEED_SWITCH);
            MiscTools.registerTrack(EnumTrack.SPEED_WYE);
        }
    }
}
