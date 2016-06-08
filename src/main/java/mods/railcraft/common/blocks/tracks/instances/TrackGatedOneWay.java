/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.common.blocks.tracks.EnumTrack;
import net.minecraft.entity.item.EntityMinecart;

public class TrackGatedOneWay extends TrackGated {

    private static final double MOTION_MIN = 0.2;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.GATED_ONEWAY;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isGateOpen()) {
            int meta = getTile().getBlockMetadata();
            if (meta == 0) {
                if (isReversed()) {
                    cart.motionZ = Math.max(Math.abs(cart.motionZ), MOTION_MIN);
                } else {
                    cart.motionZ = -Math.max(Math.abs(cart.motionZ), MOTION_MIN);
                }
            } else if (meta == 1) {
                if (isReversed()) {
                    cart.motionX = -Math.max(Math.abs(cart.motionX), MOTION_MIN);
                } else {
                    cart.motionX = Math.max(Math.abs(cart.motionX), MOTION_MIN);
                }
            }
        }
    }
}
