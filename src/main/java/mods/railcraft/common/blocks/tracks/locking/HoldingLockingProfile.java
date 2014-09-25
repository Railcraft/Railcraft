/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.locking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.tracks.TrackNextGenLocking;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HoldingLockingProfile extends LockingProfile {

    protected static float DIR_THRESHOLD = 0.01f;
    protected boolean launchForward = true;

    public HoldingLockingProfile(TrackNextGenLocking track) {
        super(track);
    }

    @Override
    public void onLock(EntityMinecart cart) {
        super.onLock(cart);
        setLaunchDirection(cart);
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
            if (launchForward)
                cart.motionZ += boostZ;
            else
                cart.motionZ -= boostZ;
        else if (meta == 1 || meta == 2 || meta == 3)
            if (launchForward)
                cart.motionX += boostX;
            else
                cart.motionX -= boostX;
    }

    protected void setLaunchDirection(EntityMinecart cart) {
        int meta = track.tileEntity.getBlockMetadata();
        double speed = CartTools.getCartSpeedUncapped(cart);
        if (speed > DIR_THRESHOLD) {
            boolean launch = launchForward;
            if (meta == 0 || meta == 4 || meta == 5)
                launch = cart.motionZ > 0;
            else if (meta == 1 || meta == 2 || meta == 3)
                launch = cart.motionX > 0;
            if (launchForward != launch) {
                launchForward = launch;
                track.sendUpdateToClient();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("launchForward", launchForward);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        launchForward = data.getBoolean("launchForward");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(launchForward);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        launchForward = data.readBoolean();

        track.markBlockNeedsUpdate();
    }

}
