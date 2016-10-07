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
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.RailcraftItems;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:locomotives", softDependencyClasses = ModuleTracks.class, description = "locomotives, locomotive related tracks, train drag")
public class ModuleLocomotives extends RailcraftModulePayload {
    public ModuleLocomotives() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            public void construction() {
                add(
                        RailcraftItems.WHISTLE_TUNER,
                        TrackKits.WHISTLE,
                        TrackKits.LOCOMOTIVE,
                        TrackKits.THROTTLE,
                        RailcraftCarts.LOCO_STEAM_SOLID,
                        RailcraftCarts.LOCO_ELECTRIC,
                        RailcraftCarts.LOCO_CREATIVE
//                        RailcraftBlocks.track
                );
            }
        });
    }
}
