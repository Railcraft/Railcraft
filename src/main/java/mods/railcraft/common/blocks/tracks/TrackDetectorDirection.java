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
import mods.railcraft.api.tracks.ITrackEmitter;
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;

public class TrackDetectorDirection extends TrackBaseRailcraft implements ITrackReversable, ITrackEmitter {
    private static final int POWER_DELAY = 10;
    private boolean reversed = false;
    private byte delay = 0;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.DETECTOR_DIRECTION;
    }

    @Override
    public IIcon getIcon() {
        if (getPowerOutput() != 0) {
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
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (Game.isNotHost(getWorld())) {
            return;
        }
        if (delay > 0) {
            delay--;
            if (delay == 0) {
                notifyNeighbors();
            }
        }
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = tileEntity.getBlockMetadata();
        if (meta == 1 || meta == 2 || meta == 3) {
            if ((isReversed() && cart.motionX < 0.0D) || (!isReversed() && cart.motionX > 0.0D)) {
                setTrackPowering();
            }
        } else if (meta == 0 || meta == 4 || meta == 5) {
            if ((isReversed() && cart.motionZ > 0.0D) || (!isReversed() && cart.motionZ < 0.0D)) {
                setTrackPowering();
            }
        }
    }

    private void notifyNeighbors() {
        getWorld().notifyBlocksOfNeighborChange(getX(), getY(), getZ(), RailcraftBlocks.getBlockTrack());
        getWorld().notifyBlocksOfNeighborChange(getX(), getY() - 1, getZ(), RailcraftBlocks.getBlockTrack());
        sendUpdateToClient();
    }

    private void setTrackPowering() {
        boolean notify = delay == 0;
        delay = POWER_DELAY;
        if (notify) {
            notifyNeighbors();
        }
    }

    @Override
    public int getPowerOutput() {
        return delay > 0 ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
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
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("direction", reversed);
        nbttagcompound.setByte("delay", delay);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("direction");
        delay = nbttagcompound.getByte("delay");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(delay);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        delay = data.readByte();
        reversed = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
