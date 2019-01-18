/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.interfaces.ITileRedstoneEmitter;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class TileBoxReceiver extends TileBoxActionManager implements IReceiverTile, IAspectProvider, ITileRedstoneEmitter {

    private static final int FORCED_UPDATE = 512;
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalBoxVariant.RECEIVER;
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.BOX_RECEIVER;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(getWorld())) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        if (clock % FORCED_UPDATE == 0) {
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
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        updateNeighbors();
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        return tile instanceof TileBoxBase && ((TileBoxBase) tile).canReceiveAspect();
    }

    @Override
    public int getPowerOutput(EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(world, getPos().offset(side.getOpposite()));
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return isEmittingRedstone(side) ? FULL_POWER : NO_POWER;
    }

    @Override
    public boolean isEmittingRedstone(EnumFacing side) {
        return doesActionOnAspect(receiver.getAspect());
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        super.doActionOnAspect(aspect, trigger);
        updateNeighbors();
    }

    @Override
    public SignalAspect getBoxSignalAspect(@Nullable EnumFacing side) {
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
