/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.Axis;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Created by CovertJaguar on 8/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTrackForce extends RailcraftTileEntity {

    @Nullable
    private TileForceTrackEmitter emitter = null;
    private int index = 0;
    private int color = BlockForceTrackEmitter.DEFAULT_SHADE;
    private boolean eastWest = false;

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

    EnumRailDirection getDirection() {
        return eastWest ? EnumRailDirection.EAST_WEST : EnumRailDirection.NORTH_SOUTH;
    }

    public void setEmitter(@Nullable TileForceTrackEmitter emitter) {
        this.emitter = emitter;
        if (emitter != null) {
            setOwner(emitter.getOwner());
            this.color = emitter.getColor();
            this.index = emitter.getNumberOfTracks();
            this.eastWest = emitter.getFacing().getAxis() == Axis.X;
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(color);
        data.writeBoolean(eastWest);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        color = data.readInt();
        eastWest = data.readBoolean();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("eastWest", eastWest);
        data.setInteger("color", color);
        data.setInteger("index", index);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        eastWest = data.getBoolean("eastWest");
        color = data.getInteger("color");
        index = data.getInteger("index");
    }
}
