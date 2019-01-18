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
import mods.railcraft.common.items.RailcraftItems;

@RailcraftModule(value = "railcraft:seasonal", description = "christmas, halloween")
public class ModuleSeasonal extends RailcraftModulePayload {

    public ModuleSeasonal() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftItems.CROWBAR_SEASONS,
                        RailcraftCarts.PUMPKIN,
                        RailcraftCarts.GIFT
                );
            }
        });
    }
}

