/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.SimpleSignalReceiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;

import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

public class TileBoxReceiver extends TileBoxActionManager implements IAspectActionManager, IGuiReturnHandler, IReceiverTile, IAspectProvider {
    private static final int FORCED_UPDATE = 512;
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_RECEIVER;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (player.isSneaking())
            return false;
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.BOX_RECEIVER, player, worldObj, xCoord, yCoord, zCoord);
        return true;
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
        if (receiver.isBeingPaired())
            receiver.setAspect(SignalAspect.BLINK_YELLOW);
        else if (!receiver.isPaired())
            receiver.setAspect(SignalAspect.BLINK_RED);
        if (prevAspect != receiver.getAspect() || clock % FORCED_UPDATE == 0) {
            updateNeighbors();
            sendUpdateToClient();
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
        updateNeighbors();
        sendUpdateToClient();
    }

    private void updateNeighbors() {
        notifyBlocksOfNeighborChange();
        updateNeighborBoxes();
    }

    @Override
    public int getPowerOutput(int side) {
        TileEntity tile = WorldPlugin.getTileEntityOnSide(worldObj, xCoord, yCoord, zCoord, MiscTools.getOppositeSide(side));
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return doesActionOnAspect(receiver.getAspect()) ? FULL_POWER : NO_POWER;
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
        markBlockForUpdate();
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        updateNeighbors();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canReceiveAspect();
        return false;
    }

    @Override
    public boolean isEmittingRedstone(ForgeDirection side) {
        return doesActionOnAspect(receiver.getAspect());
    }

    @Override
    public boolean canEmitRedstone(ForgeDirection side) {
        return true;
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        super.doActionOnAspect(aspect, trigger);
        updateNeighbors();
    }

    @Override
    public SignalAspect getBoxSignalAspect(ForgeDirection side) {
        return receiver.getAspect();
    }

    @Override
    public boolean canTransferAspect() {
        return true;
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getTriggerAspect() {
        return getBoxSignalAspect(null);
    }
}
