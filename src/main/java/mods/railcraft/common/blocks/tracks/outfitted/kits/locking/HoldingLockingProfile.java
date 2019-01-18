/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits.locking;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitLocking;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HoldingLockingProfile extends LockingProfile {

    protected static float DIR_THRESHOLD = 0.01f;
    protected boolean launchForward = true;

    public HoldingLockingProfile(TrackKitLocking track) {
        super(track);
    }

    @Override
    public void onLock(EntityMinecart cart) {
        super.onLock(cart);
        setLaunchDirection(cart);
    }

    @Override
    public void onPass(EntityMinecart cart) {
        setLaunchDirection(cart);
    }

    @Override
    public void onRelease(EntityMinecart cart) {
        super.onRelease(cart);
        BlockRailBase.EnumRailDirection trackShape = getTrackShape();
        double speed = CartToolsAPI.getCartSpeedUncapped(cart);
        double boostX = TrackKitLocking.START_BOOST;
        double boostZ = TrackKitLocking.START_BOOST;
        if (speed > 0.005D) {
            boostX = (Math.abs(cart.motionX) / speed) * TrackKitLocking.BOOST_FACTOR;
            boostZ = (Math.abs(cart.motionZ) / speed) * TrackKitLocking.BOOST_FACTOR;
        }
        if (TrackShapeHelper.isNorthSouth(trackShape)) {
            if (launchForward)
                cart.motionZ += boostZ;
            else
                cart.motionZ -= boostZ;
        } else {
            if (launchForward)
                cart.motionX += boostX;
            else
                cart.motionX -= boostX;
        }
    }

    protected void setLaunchDirection(EntityMinecart cart) {
        BlockRailBase.EnumRailDirection trackShape = getTrackShape();
        double speed = CartToolsAPI.getCartSpeedUncapped(cart);
        if (speed > DIR_THRESHOLD) {
            boolean launch;
            if (TrackShapeHelper.isNorthSouth(trackShape))
                launch = cart.motionZ > 0;
            else
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
