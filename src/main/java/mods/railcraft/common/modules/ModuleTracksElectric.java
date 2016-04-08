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

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "tracks|electric", dependencyClasses = {ModuleLocomotives.class, ModuleElectricity.class})
public class ModuleTracksElectric extends RailcraftModulePayload {

    public ModuleTracksElectric() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                RailcraftBlocks.registerBlockTrack();

                if (RailcraftBlocks.getBlockTrack() != null) {
                    MiscTools.registerTrack(EnumTrack.ELECTRIC);
                    MiscTools.registerTrack(EnumTrack.ELECTRIC_JUNCTION);
                    MiscTools.registerTrack(EnumTrack.ELECTRIC_SWITCH);
                    MiscTools.registerTrack(EnumTrack.ELECTRIC_WYE);
                }
            }
        });
    }
}
