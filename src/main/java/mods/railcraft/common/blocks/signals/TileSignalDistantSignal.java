/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.util.misc.Game;

public class TileSignalDistantSignal extends TileSignalBase implements IReceiverTile {
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.DISTANT_SIGNAL;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld())) {
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

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        receiver.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
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
