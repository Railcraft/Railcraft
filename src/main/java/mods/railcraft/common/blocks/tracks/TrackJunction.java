/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.entity.item.EntityMinecart;

public class TrackJunction extends TrackBaseRailcraft {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.JUNCTION;
    }

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public int getBasicRailMetadata(EntityMinecart cart) {
        if (cart == null) {
            return EnumTrackMeta.NORTH_SOUTH.ordinal();
        }
        float yaw = cart.prevRotationYaw;
        yaw = yaw % 180;
        while (yaw < 0) {
            yaw += 180;
        }
        if ((yaw >= 45) && (yaw <= 135)) {
            return EnumTrackMeta.NORTH_SOUTH.ordinal();
        }
        return EnumTrackMeta.EAST_WEST.ordinal();
    }
}
