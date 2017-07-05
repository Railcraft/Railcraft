/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileSignalDistantSignal extends TileSignalBase implements IReceiverTile {

    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);


    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalVariant.DISTANT;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(getWorld())) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        SignalAspect prevAspect = receiver.getAspect();
        if (receiver.isBeingPaired()) {
            receiver.setAspect(SignalAspect.BLINK_YELLOW);
        } else if (!receiver.isPaired()) {
            receiver.setAspect(SignalAspect.BLINK_RED);
        }
        if (prevAspect != receiver.getAspect()) {
            sendUpdateToClient();
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
        sendUpdateToClient();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        receiver.readPacketData(data);
    }

    @Override
    public SignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getSignalAspect() {
        return receiver.getAspect();
    }
}
