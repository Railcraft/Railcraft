/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.locking;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.tracks.TrackNextGenLocking;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BoardingLockingProfile extends LockingProfile {

    public BoardingLockingProfile(TrackNextGenLocking track) {
        super(track);
    }

    @Override
    public void onRelease(EntityMinecart cart) {
        super.onRelease(cart);
        int meta = track.tileEntity.getBlockMetadata();
        double speed = CartTools.getCartSpeedUncapped(cart);
        double boostX = TrackNextGenLocking.START_BOOST;
        double boostZ = TrackNextGenLocking.START_BOOST;
        if (speed > 0.005D) {
            boostX = (Math.abs(cart.motionX) / speed) * TrackNextGenLocking.BOOST_FACTOR;
            boostZ = (Math.abs(cart.motionZ) / speed) * TrackNextGenLocking.BOOST_FACTOR;
        }
        if (meta == 0 || meta == 4 || meta == 5)
            if (isReversed())
                cart.motionZ += boostZ;
            else
                cart.motionZ -= boostZ;
        else if (meta == 1 || meta == 2 || meta == 3)
            if (isReversed())
                cart.motionX -= boostX;
            else
                cart.motionX += boostX;
    }
    
    private boolean isReversed(){
        return track.getProfileType() == TrackNextGenLocking.LockingProfileType.BOARDING_B || track.getProfileType() == TrackNextGenLocking.LockingProfileType.BOARDING_B_TRAIN;
    }

}
