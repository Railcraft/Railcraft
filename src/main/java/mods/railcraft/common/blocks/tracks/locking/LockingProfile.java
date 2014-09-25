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
import mods.railcraft.common.blocks.tracks.TrackNextGenLocking;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class LockingProfile {

    protected final TrackNextGenLocking track;

    public LockingProfile(TrackNextGenLocking track) {
        this.track = track;
    }

    public void onLock(EntityMinecart cart) {
    }

    public void onRelease(EntityMinecart cart) {
    }

    public void writeToNBT(NBTTagCompound data) {
    }

    public void readFromNBT(NBTTagCompound data) {
    }

    public void writePacketData(DataOutputStream data) throws IOException {
    }

    public void readPacketData(DataInputStream data) throws IOException {
    }

}
