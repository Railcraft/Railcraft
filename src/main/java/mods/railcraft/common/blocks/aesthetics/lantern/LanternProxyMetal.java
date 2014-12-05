package mods.railcraft.common.blocks.aesthetics.lantern;

import java.util.List;

public class LanternProxyMetal implements LanternProxy {

    @Override
    public List<? extends LanternInfo> getCreativeList() {
        return EnumLanternMetal.creativeList;
    }

    @Override
    public LanternInfo fromOrdinal(int id) {
        return EnumLanternMetal.fromOrdinal(id);
    }

}
