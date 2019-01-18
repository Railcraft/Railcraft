/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;

public class TrackKitActivator extends TrackKitPowered {

    private static final int POWER_PROPAGATION = 8;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.ACTIVATOR;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        BlockPos pos = getPos();
        cart.onActivatorRailPass(pos.getX(), pos.getY(), pos.getZ(), isPowered());
    }

    @Override
    public int getPowerPropagation() {
        return POWER_PROPAGATION;
    }
}
