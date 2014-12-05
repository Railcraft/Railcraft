package mods.railcraft.common.blocks.aesthetics.lantern;

import java.util.List;

public interface LanternProxy {

    List<? extends LanternInfo> getCreativeList();

    LanternInfo fromOrdinal(int id);

}
