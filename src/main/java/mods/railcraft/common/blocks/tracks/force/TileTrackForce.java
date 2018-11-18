/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.force;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.single.BlockForceTrackEmitter;
import mods.railcraft.common.blocks.single.TileForceTrackEmitter;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Created by CovertJaguar on 8/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTrackForce extends RailcraftTileEntity {

    private @Nullable TileForceTrackEmitter emitter;
    private int index;
    private int color = BlockForceTrackEmitter.DEFAULT_SHADE;

    int getColor() {
        return color;
    }

    void notifyEmitterForBreak() {
        if (emitter != null) {
            emitter.clearTracks(index);
        }
    }

    void notifyEmitterForTrackChange() {
        if (emitter != null) {
            emitter.notifyTrackChange();
        }
    }

    public void setEmitter(@Nullable TileForceTrackEmitter emitter) {
        this.emitter = emitter;
        if (emitter != null) {
            setOwner(emitter.getOwner());
            this.color = emitter.getColor();
            this.index = emitter.getNumberOfTracks();
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(color);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        color = data.readInt();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("color", color);
        data.setInteger("index", index);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        color = data.getInteger("color");
        index = data.getInteger("index");
    }
}
