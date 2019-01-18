/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftCarts.WORK,
                        RailcraftCarts.JUKEBOX,
                        RailcraftCarts.BED
                );
            }
        });
    }
}
