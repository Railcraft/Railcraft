/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule(value = "railcraft:tracks|high_speed_electric",
        dependencyClasses = {ModuleLocomotives.class, ModuleElectricity.class, ModuleTracksElectric.class, ModuleTracksHighSpeed.class})
public class ModuleTracksHighSpeedElectric extends RailcraftModulePayload {

    public ModuleTracksHighSpeedElectric() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC
//                        RailcraftBlocks.TRACK_JUNCTION_HS_ELECTRIC
                );
            }
        });
    }
}
