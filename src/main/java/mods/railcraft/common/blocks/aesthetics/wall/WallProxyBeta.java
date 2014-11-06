/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.wall;

import java.util.List;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WallProxyBeta implements WallProxy {

    @Override
    public List<? extends WallInfo> getCreativeList() {
        return EnumWallBeta.getCreativeList();
    }

    @Override
    public WallInfo fromMeta(int meta) {
        return EnumWallBeta.fromMeta(meta);
    }

}
