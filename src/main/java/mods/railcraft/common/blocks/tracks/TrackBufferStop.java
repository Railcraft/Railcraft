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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import mods.railcraft.api.tracks.ITrackCustomShape;
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.util.misc.MiscTools;

public class TrackBufferStop extends TrackBaseRailcraft implements ITrackReversable, ITrackCustomShape {

    private static final float CBOX = 0.0625f;
    private static final float SBOX = 0.0625f * 3;
    private static final float SBOXY = 0.0625f * 5;
    private boolean reversed = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.BUFFER_STOP;
    }

    @Override
    public IIcon getIcon() {
        if (reversed) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool() {
        return AxisAlignedBB.getBoundingBox(tileEntity.xCoord + SBOX, tileEntity.yCoord, tileEntity.zCoord + SBOX, tileEntity.xCoord + 1 - SBOX, tileEntity.yCoord + 1 - SBOXY, tileEntity.zCoord + 1 - SBOX);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(Vec3 vec3d, Vec3 vec3d1) {
        return MiscTools.collisionRayTrace(vec3d, vec3d1, getX(), getY(), getZ());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {
        return AxisAlignedBB.getBoundingBox(tileEntity.xCoord + CBOX, tileEntity.yCoord, tileEntity.zCoord + CBOX, tileEntity.xCoord + 1 - CBOX, tileEntity.yCoord + 1, tileEntity.zCoord + 1 - CBOX);
    }

    @Override
    public boolean canMakeSlopes() {
        return false;
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
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("direction");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean r = data.readBoolean();

        if (reversed != r) {
            reversed = r;
            markBlockNeedsUpdate();
        }
    }
}
