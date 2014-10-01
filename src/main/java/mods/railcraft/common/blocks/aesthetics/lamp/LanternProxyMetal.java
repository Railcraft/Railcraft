package mods.railcraft.common.blocks.aesthetics.lamp;

import mods.railcraft.common.blocks.aesthetics.lamp.EnumLanternMetal;

public class LanternProxyMetal implements LanternProxy {

    @Override
    public LanternInfo[] values() {
        return EnumLanternMetal.VALUES;
    }

    @Override
    public LanternInfo fromOrdinal(int id) {
        return EnumLanternMetal.fromOrdinal(id);
    }

}
