/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;

public class TrackKitGatedOneWay extends TrackKitGated {

    private static final double MOTION_MIN = 0.2;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.GATED;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isGateOpen()) {
            BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirectionRaw(theWorldAsserted(), getPos());
            if (shape == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                double motion = Math.max(Math.abs(cart.motionZ), MOTION_MIN);
                cart.motionZ = motion * (isReversed() ? 1 : -1);
            } else {
                double motion = Math.max(Math.abs(cart.motionX), MOTION_MIN);
                cart.motionX = motion * (isReversed() ? -1 : 1);
            }
        }
    }
}
