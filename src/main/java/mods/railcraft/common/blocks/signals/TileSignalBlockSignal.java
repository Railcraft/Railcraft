/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftDataInputStream;
import mods.railcraft.common.util.network.RailcraftDataOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileSignalBlockSignal extends TileSignalBase implements IControllerTile, ISignalBlockTile {

    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private final SignalBlock signalBlock = new SignalBlockSimple(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BLOCK_SIGNAL;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isNotHost(worldObj)) {
            controller.tickClient();
            signalBlock.tickClient();
            return;
        }
        controller.tickServer();
        signalBlock.tickServer();
        SignalAspect prevAspect = controller.getAspect();
        if (controller.isBeingPaired()) {
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        } else {
            controller.setAspect(signalBlock.getSignalAspect());
        }
        if (prevAspect != controller.getAspect()) {
            sendUpdateToClient();
        }
        if (SignalTools.printSignalDebug && prevAspect != SignalAspect.BLINK_RED && controller.getAspect() == SignalAspect.BLINK_RED) {
            Game.log(Level.INFO, "Signal Tile changed aspect to BLINK_RED: source:[{0}]", getPos());
        }
    }

    @Override
    public SignalAspect getSignalAspect() {
        return controller.getAspect();
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        try {
            super.writeToNBT(data);
            signalBlock.writeToNBT(data);
            controller.writeToNBT(data);
        } catch (Throwable er) {
            Game.logThrowable(Level.ERROR, "Signal Tile crashed on write.", 10, er);
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        try {
            super.readFromNBT(data);
            signalBlock.readFromNBT(data);
            controller.readFromNBT(data);
        } catch (Throwable er) {
            Game.logThrowable(Level.ERROR, "Signal Tile crashed on read.", 10, er);
        }
    }

    @Override
    public void writePacketData(@Nonnull RailcraftDataOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        signalBlock.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftDataInputStream data) throws IOException {
        super.readPacketData(data);
        controller.readPacketData(data);
        signalBlock.readPacketData(data);
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }

    @Override
    public SignalBlock getSignalBlock() {
        return signalBlock;
    }
}
