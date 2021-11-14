/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DualSignalReceiver extends SignalReceiver {
    @Nonnull
    private SignalAspect topAspect = SignalAspect.BLINK_RED;
    @Nonnull
    private SignalAspect bottomAspect = SignalAspect.BLINK_RED;

    public DualSignalReceiver(String locTag, TileEntity tile) {
        super(locTag, tile, 2);
    }

    @Override
    public void onControllerAspectChange(SignalController con, @Nonnull SignalAspect aspect) {
        WorldCoordinate coord = pairings.peekFirst();
        if (coord == null) {
            return;
        }
        if (coord.equals(con.getCoords())) {
            if (aspect != topAspect) {
                topAspect = aspect;
                super.onControllerAspectChange(con, aspect);
            }
        } else {
            if (aspect != bottomAspect) {
                bottomAspect = aspect;
                super.onControllerAspectChange(con, aspect);
            }
        }
    }

    @Override
    protected void saveNBT(NBTTagCompound data) {
        super.saveNBT(data);
        data.setByte("topAspect", (byte) topAspect.ordinal());
        data.setByte("bottomAspect", (byte) bottomAspect.ordinal());
    }

    @Override
    protected void loadNBT(NBTTagCompound data) {
        super.loadNBT(data);
        topAspect = SignalAspect.values()[data.getByte("topAspect")];
        bottomAspect = SignalAspect.values()[data.getByte("bottomAspect")];
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(topAspect.ordinal());
        data.writeByte(bottomAspect.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        topAspect = SignalAspect.values()[data.readByte()];
        bottomAspect = SignalAspect.values()[data.readByte()];
    }

    @Nonnull
    public SignalAspect getTopAspect() {
        return topAspect;
    }

    @Nonnull
    public SignalAspect getBottomAspect() {
        return bottomAspect;
    }

    public boolean setTopAspect(@Nonnull SignalAspect aspect) {
        if (topAspect != aspect) {
            topAspect = aspect;
            return true;
        }
        return false;
    }

    public boolean setBottomAspect(@Nonnull SignalAspect aspect) {
        if (bottomAspect != aspect) {
            bottomAspect = aspect;
            return true;
        }
        return false;
    }
}
