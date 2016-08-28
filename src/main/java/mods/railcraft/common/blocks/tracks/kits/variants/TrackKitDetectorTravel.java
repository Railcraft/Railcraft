/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitDetectorTravel extends TrackKitDetector implements ITrackKitReversible {
    private boolean reversed;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DETECTOR_TRAVEL;
    }

    @Override
    public int getRenderState() {
        int state = getPowerOutput() > 0 ? 1 : 0;
        if (isReversed())
            state += 2;
        return state;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        BlockRailBase.EnumRailDirection shape = getRailDirection();
        if (TrackShapeHelper.isEastWest(shape)) {
            if (isReversed() ? cart.motionX < 0.0D : cart.motionX > 0.0D) {
                setTrackPowering();
            }
        } else if (TrackShapeHelper.isNorthSouth(shape)) {
            if (isReversed() ? cart.motionZ > 0.0D : cart.motionZ < 0.0D) {
                setTrackPowering();
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
