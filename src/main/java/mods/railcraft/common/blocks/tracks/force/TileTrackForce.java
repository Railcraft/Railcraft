/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.force;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.single.BlockForceTrackEmitter;
import mods.railcraft.common.blocks.single.TileForceTrackEmitter;
import mods.railcraft.common.plugins.color.EnumColor;
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
public final class TileTrackForce extends TileRailcraft {

    private @Nullable TileForceTrackEmitter emitter;
    private int index;
    private EnumColor color = BlockForceTrackEmitter.DEFAULT_COLOR;

    EnumColor getColor() {
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
        data.writeEnum(color);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        color = data.readEnum(EnumColor.VALUES);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        color.writeToNBT(data);
        data.setInteger("index", index);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        color = EnumColor.readFromNBT(data).orElse(BlockForceTrackEmitter.DEFAULT_COLOR);
        index = data.getInteger("index");
    }
}
