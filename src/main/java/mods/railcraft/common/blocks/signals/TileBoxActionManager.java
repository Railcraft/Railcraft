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
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class TileBoxActionManager extends TileBoxSecured implements IAspectActionManager, IGuiReturnHandler {

    private final boolean[] powerOnAspects = new boolean[SignalAspect.values().length];

    public TileBoxActionManager() {
        powerOnAspects[SignalAspect.GREEN.ordinal()] = true;
    }

    @Override
    public boolean doesActionOnAspect(SignalAspect aspect) {
        return powerOnAspects[aspect.ordinal()];
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        powerOnAspects[aspect.ordinal()] = trigger;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        byte[] array = new byte[powerOnAspects.length];
        for (int i = 0; i < powerOnAspects.length; i++) {
            array[i] = (byte) (powerOnAspects[i] ? 1 : 0);
        }
        data.setByteArray("powerOnAspects", array);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("PowerOnAspect")) {
            byte[] array = data.getByteArray("PowerOnAspect");
            for (int i = 0; i < powerOnAspects.length; i++) {
                powerOnAspects[i] = array[i] == 1;
            }
        } else if (data.hasKey("powerOnAspects")) {
            byte[] array = data.getByteArray("powerOnAspects");
            for (int i = 0; i < powerOnAspects.length; i++) {
                powerOnAspects[i] = array[i] == 1;
            }
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        writeActionInfo(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        readActionInfo(data.readByte());
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        super.writeGuiData(data);
        writeActionInfo(data);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        byte bits = data.readByte();
        if (sender == null || canAccess(sender.getGameProfile())) {
            readActionInfo(bits);
        }
    }

    private void writeActionInfo(DataOutputStream data) throws IOException {
        byte bits = 0;
        for (int i = 0; i < powerOnAspects.length; i++) {
            bits |= (powerOnAspects[i] ? 1 : 0) << i;
        }
        data.writeByte(bits);
    }

    private void readActionInfo(byte bits) {
        for (int bit = 0; bit < powerOnAspects.length; bit++) {
            powerOnAspects[bit] = ((bits >> bit) & 1) == 1;
        }
    }

}
