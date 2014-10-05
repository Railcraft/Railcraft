package mods.railcraft.common.blocks.aesthetics.lantern;

public class LanternProxyStone implements LanternProxy {

    @Override
    public LanternInfo[] values() {
        return EnumLanternStone.VALUES;
    }

    @Override
    public LanternInfo fromOrdinal(int id) {
        return EnumLanternStone.fromOrdinal(id);
    }
}
