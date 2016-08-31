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

@RailcraftModule("railcraft:extras")
public class ModuleExtras extends RailcraftModulePayload {
    public ModuleExtras() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        TrackKits.PRIMING,
                        TrackKits.LAUNCHER,
                        RailcraftCarts.TNT_WOOD,
                        RailcraftCarts.WORK
//                        RailcraftBlocks.track
//                        RailcraftBlocks.trackElevator
                );
            }
        });
    }
}
