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
import mods.railcraft.common.items.RailcraftItems;
import net.minecraftforge.common.config.Configuration;

@RailcraftModule(value = "railcraft:seasonal", description = "christmas, halloween, ghost trains")
public class ModuleSeasonal extends RailcraftModulePayload {
    public static Config config;

    public ModuleSeasonal() {
        add(
                RailcraftItems.CROWBAR_SEASONS,
                RailcraftCarts.PUMPKIN,
                RailcraftCarts.GIFT
        );
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleSeasonal.config = new Config(config);
    }

    public static class Config {
        public final int christmas;
        public final int halloween;
        public final int harvest;

        public Config(Configuration config) {
            christmas = config.getInt("christmas", CAT_CONFIG, 0, 0, 2,
                    "Controls whether Christmas mode is (0) enabled, (1) forced, or (2) disabled");
            halloween = config.getInt("halloween", CAT_CONFIG, 0, 0, 2,
                    "Controls whether Halloween mode is (0) enabled, (1) forced, or (2) disabled");
            harvest = config.getInt("harvest", CAT_CONFIG, 0, 0, 2,
                    "Controls whether Harvest mode is (0) enabled, (1) forced, or (2) disabled");
        }
    }
}

