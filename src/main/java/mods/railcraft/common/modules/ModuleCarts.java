/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.carts.RailcraftCarts;

@RailcraftModule(value = "railcraft:carts", description = "railcraft custom carts")
public class ModuleCarts extends RailcraftModulePayload {
    public ModuleCarts() {
        add(
                RailcraftCarts.WORK,
                RailcraftCarts.JUKEBOX,
                RailcraftCarts.BED
        );
    }
}
