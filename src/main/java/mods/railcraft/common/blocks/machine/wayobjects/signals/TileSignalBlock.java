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
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TileSignalBlock extends TileSignalBase implements IControllerTile, ISignalTileBlock {

    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private final SignalBlock signalBlock = new SignalBlockSimple(getLocalizationTag(), this);

    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalVariant.BLOCK;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
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
            Game.log().msg(Level.INFO, "Signal Tile changed aspect to BLINK_RED: source:[{0}]", getPos());
        }
    }

    @Override
    public SignalAspect getSignalAspect() {
        return controller.getAspect();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        try {
            super.writeToNBT(data);
            signalBlock.writeToNBT(data);
            controller.writeToNBT(data);
        } catch (Throwable er) {
            Game.log().throwable(Level.ERROR, 10, er, "Signal Tile crashed on write.");
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        try {
            super.readFromNBT(data);
            signalBlock.readFromNBT(data);
            controller.readFromNBT(data);
        } catch (Throwable er) {
            Game.log().throwable(Level.ERROR, 10, er, "Signal Tile crashed on read.");
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        signalBlock.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
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
