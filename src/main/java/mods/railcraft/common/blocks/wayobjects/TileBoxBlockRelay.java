/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
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

public class TileBoxBlockRelay extends TileBoxActionManager implements ISignalBlockTile, IAspectProvider {

    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private final SignalBlock signalBlock = new SignalBlockRelay(getLocalizationTag(), this);

    @Override
    public EnumWayObject getSignalType() {
        return EnumWayObject.BOX_BLOCK_RELAY;
    }

    @Override
    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (player.isSneaking())
            return false;
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.BOX_RELAY, player, worldObj, getX(), getY(), getZ());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj)) {
            controller.tickClient();
            signalBlock.tickClient();
            return;
        }
        controller.tickServer();
        signalBlock.tickServer();
        SignalAspect prevAspect = controller.getAspect();
        if (controller.isBeingPaired())
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        else
            controller.setAspect(signalBlock.getSignalAspect());
        if (prevAspect != controller.getAspect()) {
            updateNeighbors();
            sendUpdateToClient();
        }
    }

    private void updateNeighbors() {
        notifyBlocksOfNeighborChange();
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof TileBoxBase) {
                TileBoxBase box = (TileBoxBase) tile;
                box.onNeighborStateChange(this, side);
            }
        }
    }

    @Override
    public int getPowerOutput(EnumFacing side) {
        TileEntity tile = WorldPlugin.getBlockTile(worldObj, getPos().offset(side.getOpposite()));
        if (tile instanceof TileBoxBase)
            return NO_POWER;
        return isEmittingRedstone(side) ? FULL_POWER : NO_POWER;
    }

    @Override
    public boolean isEmittingRedstone(EnumFacing side) {
        return doesActionOnAspect(getBoxSignalAspect(side));
    }

    @Override
    public boolean canEmitRedstone(EnumFacing side) {
        return true;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        signalBlock.writeToNBT(data);
        controller.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        signalBlock.readFromNBT(data);
        controller.readFromNBT(data);
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        signalBlock.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        controller.readPacketData(data);
        signalBlock.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        updateNeighbors();
    }

//    @Override
//    public SimpleSignalController getController() {
//        return controller;
//    }

    @Override
    public SignalBlock getSignalBlock() {
        return signalBlock;
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        super.doActionOnAspect(aspect, trigger);
        updateNeighbors();
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        return tile instanceof TileBoxBase && ((TileBoxBase) tile).canReceiveAspect();
    }

    @Override
    public SignalAspect getBoxSignalAspect(@Nullable EnumFacing side) {
        return controller.getAspect();
    }

    @Override
    public boolean canTransferAspect() {
        return true;
    }

    @Override
    public SignalAspect getTriggerAspect() {
        return getBoxSignalAspect(null);
    }

}
