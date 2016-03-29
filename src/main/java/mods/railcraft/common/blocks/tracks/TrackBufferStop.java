/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackCustomShape;
import mods.railcraft.api.tracks.ITrackReversible;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackBufferStop extends TrackBaseRailcraft implements ITrackReversible, ITrackCustomShape {

    private static final float CBOX = 0.0625f;
    private static final float SBOX = 0.0625f * 3;
    private static final float SBOXY = 0.0625f * 5;
    private boolean reversed = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.BUFFER_STOP;
    }

//    @Override
//    public IIcon getIcon() {
//        if (reversed) {
//            return getIcon(1);
//        }
//        return getIcon(0);
//    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool() {
        double x = tileEntity.getPos().getX();
        double y = tileEntity.getPos().getY();
        double z = tileEntity.getPos().getZ();
        return AxisAlignedBB.fromBounds(x + SBOX, y, z + SBOX, x + 1 - SBOX, y + 1 - SBOXY, z + 1 - SBOX);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(Vec3 vec3d, Vec3 vec3d1) {
        return MiscTools.collisionRayTrace(vec3d, vec3d1, getPos());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {
        double x = tileEntity.getPos().getX();
        double y = tileEntity.getPos().getY();
        double z = tileEntity.getPos().getZ();
        return AxisAlignedBB.fromBounds(x + CBOX, y, z + CBOX, x + 1 - CBOX, y + 1, z + 1 - CBOX);
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
