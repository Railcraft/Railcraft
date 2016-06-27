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
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

public class TileBoxReceiver extends TileBoxActionManager implements IAspectActionManager, IGuiReturnHandler, IReceiverTile, IAspectProvider {

    private static final int FORCED_UPDATE = 512;
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_RECEIVER;
    }

    @Override
    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (player.isSneaking())
            return false;
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.BOX_RECEIVER, player, worldObj, getPos());
        return true;
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
    public int getPowerOutput(EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(worldObj, getPos().offset(side.getOpposite()));
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return doesActionOnAspect(receiver.getAspect()) ? FULL_POWER : NO_POWER;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        receiver.writeToNBT(data);
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
        markBlockForUpdate();
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        updateNeighbors();
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        return tile instanceof TileBoxBase && ((TileBoxBase) tile).canReceiveAspect();
    }

    @Override
    public boolean isEmittingRedstone(EnumFacing side) {
        return doesActionOnAspect(receiver.getAspect());
    }

    @Override
    public boolean canEmitRedstone(EnumFacing side) {
        return true;
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        super.doActionOnAspect(aspect, trigger);
        updateNeighbors();
    }

    @Override
    public SignalAspect getBoxSignalAspect(EnumFacing side) {
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
