/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.util.misc.Game;

public class TrackBoarding extends TrackLockingBase implements ITrackReversable {

    protected boolean reversed = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.BOARDING;
    }

    @Override
    public void updateEntity() {
        if (Game.isHost(getWorld())) {
            TrackNextGenLocking.LockingProfileType type;
            if (isReversed())
                type = TrackNextGenLocking.LockingProfileType.BOARDING_B;
            else
                type = TrackNextGenLocking.LockingProfileType.BOARDING_A;
            migrateTrack(type);
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("direction", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        reversed = data.getBoolean("direction");
    }

}
