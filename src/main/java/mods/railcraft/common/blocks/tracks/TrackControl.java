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

public class TrackControl extends TrackBaseRailcraft implements ITrackPowered, ITrackReversable {

    private boolean powered = false;
    private boolean reversed = false;
    private static final double BOOST_AMOUNT = 0.02;
    private static final double SLOW_AMOUNT = 0.02;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.CONTROL;
    }

    @Override
    public IIcon getIcon() {
        if (isPowered() ^ reversed) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (meta == 0 || meta == 4 || meta == 5) {
            if (cart.motionZ <= 0) {
                if (isPowered() ^ !reversed) {
                    cart.motionZ -= BOOST_AMOUNT;
                } else {
                    cart.motionZ += SLOW_AMOUNT;
                }
            } else if (cart.motionZ >= 0) {
                if (!isPowered() ^ !reversed) {
                    cart.motionZ += BOOST_AMOUNT;
                } else {
                    cart.motionZ -= SLOW_AMOUNT;
                }
            }
        } else if (meta == 1 || meta == 2 || meta == 3) {
            if (cart.motionX <= 0) {
                if (isPowered() ^ reversed) {
                    cart.motionX -= BOOST_AMOUNT;
                } else {
                    cart.motionX += SLOW_AMOUNT;
                }
            } else if (cart.motionX >= 0) {
                if (!isPowered() ^ reversed) {
                    cart.motionX += BOOST_AMOUNT;
                } else {
                    cart.motionX -= SLOW_AMOUNT;
                }
            }
        }
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
        nbttagcompound.setBoolean("powered", powered);
        nbttagcompound.setBoolean("reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
        reversed = nbttagcompound.getBoolean("reversed");
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

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
}
