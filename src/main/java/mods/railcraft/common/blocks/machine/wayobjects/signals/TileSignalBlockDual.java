/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class TileSignalBlockDual extends TileSignalBlock implements IReceiverTile, IDualHeadSignal {

    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalDualVariant.BLOCK;
    }

    @Override
    public int getLightValue() {
        return Math.max(getSignalAspect().getLightValue(), getSignalAspect(DualLamp.BOTTOM).getLightValue());
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
        sendUpdateToClient();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        receiver.readPacketData(data);
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getSignalAspect(DualLamp lamp) {
        if (lamp == DualLamp.BOTTOM)
            return receiver.getAspect();
        return getSignalAspect();
    }

}
