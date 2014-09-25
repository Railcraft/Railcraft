/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.api.tracks.ITrackReversable;

public class TrackOneWay extends TrackBaseRailcraft implements ITrackPowered, ITrackReversable {

    private boolean reversed = false;
    private boolean powered = false;
    private static final double LOSS_FACTOR = 0.49D;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.ONEWAY;
    }

    @Override
    public IIcon getIcon() {
        if (!isPowered()) {
            if (isReversed()) {
                return getIcon(3);
            }
            return getIcon(1);
        }
        if (isReversed()) {
            return getIcon(2);
        }
        return getIcon(0);
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (isPowered()) {
            if (meta == 1 || meta == 2 || meta == 3) {
                if ((isReversed() && cart.motionX > 0.0D) || (!isReversed() && cart.motionX < 0.0D)) {
                    double distX = cart.posX - (tileEntity.xCoord + 0.5D);
//                    System.out.println("cartX=" + cart.posX + ", railX=" + (i + 0.5D) + ", railDir=" + isReversed());
                    if (!isReversed() && distX < -0.01 || isReversed() && distX > 0.01) {
//                        System.out.println("Setting Position");
                        cart.setPosition(tileEntity.xCoord + 0.5D, cart.posY, cart.posZ);
                    }
//                    System.out.println("mX= " + cart.motionX + ", dist=" + distX);
                    if (!isReversed()) {
                        cart.motionX = Math.abs(cart.motionX) * LOSS_FACTOR;
                    } else {
                        cart.motionX = -Math.abs(cart.motionX) * LOSS_FACTOR;
                    }
                }
            } else if (meta == 0 || meta == 4 || meta == 5) {
                if ((isReversed() && cart.motionZ < 0.0D) || (!isReversed() && cart.motionZ > 0.0D)) {
                    double distZ = cart.posZ - (tileEntity.zCoord + 0.5D);
//                    System.out.println("cartZ=" + cart.posZ + ", railZ=" + (k + 0.5D) + ", railDir=" + isReversed());
                    if (isReversed() && distZ < -0.01 || !isReversed() && distZ > 0.01) {
//                        System.out.println("Setting Position");
                        cart.setPosition(cart.posX, cart.posY, tileEntity.zCoord + 0.5D);
                    }
//                    System.out.println("mZ= " + cart.motionZ + ", dist=" + distZ);
                    if (isReversed()) {
                        cart.motionZ = Math.abs(cart.motionZ) * LOSS_FACTOR;
                    } else {
                        cart.motionZ = -Math.abs(cart.motionZ) * LOSS_FACTOR;
                    }
                }
            }
        }
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("direction", reversed);
        nbttagcompound.setBoolean("powered", powered);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("direction");
        powered = nbttagcompound.getBoolean("powered");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        powered = data.readBoolean();
        reversed = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
