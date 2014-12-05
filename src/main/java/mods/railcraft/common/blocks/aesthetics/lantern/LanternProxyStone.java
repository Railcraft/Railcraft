package mods.railcraft.common.blocks.aesthetics.lantern;

import java.util.List;

public class LanternProxyStone implements LanternProxy {

    @Override
    public List<? extends LanternInfo> getCreativeList() {
        return EnumLanternStone.creativeList;
    }

    @Override
    public LanternInfo fromOrdinal(int id) {
        return EnumLanternStone.fromOrdinal(id);
    }
}
