/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import java.util.EnumSet;
import java.util.Set;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.util.misc.MiscTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleTracksElectric extends RailcraftModule {

    @Override
    public Set<ModuleManager.Module> getDependencies() {
        return EnumSet.of(ModuleManager.Module.LOCOMOTIVES, ModuleManager.Module.ELECTRICITY);
    }

    @Override
    public void initFirst() {
        RailcraftBlocks.registerBlockTrack();

        if (RailcraftBlocks.getBlockTrack() != null) {
            MiscTools.registerTrack(EnumTrack.ELECTRIC);
            MiscTools.registerTrack(EnumTrack.ELECTRIC_JUNCTION);
            MiscTools.registerTrack(EnumTrack.ELECTRIC_SWITCH);
            MiscTools.registerTrack(EnumTrack.ELECTRIC_WYE);
            MiscTools.registerTrack(EnumTrack.FORCE);
        }
    }

}
