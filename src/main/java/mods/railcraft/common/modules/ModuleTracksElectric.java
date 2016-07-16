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

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:tracks|electric", dependencyClasses = {ModuleLocomotives.class, ModuleElectricity.class})
public class ModuleTracksElectric extends RailcraftModulePayload {

    public ModuleTracksElectric() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(RailcraftBlocks.track);
            }

            @Override
            public void preInit() {
                EnumTrack.ELECTRIC.register();
                EnumTrack.ELECTRIC_JUNCTION.register();
                EnumTrack.ELECTRIC_SWITCH.register();
                EnumTrack.ELECTRIC_WYE.register();

            }
        });
    }
}
