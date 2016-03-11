/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileSwitchMotor extends TileSwitchSecured implements IAspectActionManager, IGuiReturnHandler, IReceiverTile {

    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);
    private boolean[] switchOnAspects = new boolean[SignalAspect.values().length];
    private boolean switchAspect;
    private boolean switchOnRedstone = true;

    public TileSwitchMotor() {
        switchOnAspects[SignalAspect.RED.ordinal()] = true;
    }

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.SWITCH_MOTOR;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.SWITCH_MOTOR, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        boolean active = isSwitchAspect();
        if (switchAspect != active) {
            switchAspect = active;
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power) {
            setPowered(power);
        }
    }

    @Override
    public boolean canConnectRedstone(int dir) {
        return true;
    }

    private boolean isSwitchAspect() {
        return switchOnAspects[receiver.getAspect().ordinal()];
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        byte[] array = new byte[switchOnAspects.length];
        for (int i = 0; i < switchOnAspects.length; i++) {
            array[i] = (byte) (switchOnAspects[i] ? 1 : 0);
        }
        data.setByteArray("PowerOnAspect", array);

        data.setBoolean("switchAspect", switchAspect);

        data.setBoolean("switchOnRedstone", switchOnRedstone);

        receiver.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("PowerOnAspect")) {
            byte[] array = data.getByteArray("PowerOnAspect");
            for (int i = 0; i < switchOnAspects.length; i++) {
                switchOnAspects[i] = array[i] == 1;
            }
        }

        switchAspect = data.getBoolean("switchAspect");

        if (data.hasKey("switchOnRedstone"))
            switchOnRedstone = data.getBoolean("switchOnRedstone");

        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        receiver.writePacketData(data);

        writeGuiData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        receiver.readPacketData(data);

        readGuiData(data, null);

        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        super.writeGuiData(data);
        byte bits = 0;
        for (int i = 0; i < switchOnAspects.length; i++) {
            bits |= (switchOnAspects[i] ? 1 : 0) << i;
        }
        data.writeByte(bits);
        data.writeBoolean(switchOnRedstone);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        byte bits = data.readByte();
        for (int bit = 0; bit < switchOnAspects.length; bit++) {
            switchOnAspects[bit] = ((bits >> bit) & 1) == 1;
        }
        switchOnRedstone = data.readBoolean();
    }

    @Override
    public boolean doesActionOnAspect(SignalAspect aspect) {
        return switchOnAspects[aspect.ordinal()];
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        switchOnAspects[aspect.ordinal()] = trigger;
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart) {
        return switchAspect || (shouldSwitchOnRedstone() && isPowered());
    }

    public boolean shouldSwitchOnRedstone() {
        return switchOnRedstone;
    }

    public void setSwitchOnRedstone(boolean switchOnRedstone) {
        this.switchOnRedstone = switchOnRedstone;
    }
}