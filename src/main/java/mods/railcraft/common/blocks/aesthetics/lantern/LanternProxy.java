package mods.railcraft.common.blocks.aesthetics.lantern;

import mods.railcraft.common.blocks.aesthetics.wall.WallInfo;

public interface LanternProxy {

    public LanternInfo[] values();

    LanternInfo fromOrdinal(int id);

}
