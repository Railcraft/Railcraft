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
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static net.minecraftforge.common.util.ForgeDirection.*;

public class TileBoxInterlock extends TileBoxBase implements IControllerTile, IReceiverTile, IAspectProvider {
    private static final ForgeDirection[] SIDES = {NORTH, WEST, SOUTH, EAST};
    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);
    private Interlock interlock = new Interlock(this);
    private SignalAspect overrideAspect = SignalAspect.RED;

    public TileBoxInterlock() {
    }

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.BOX_INTERLOCK;
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) {
            controller.tickClient();
            receiver.tickClient();
            return;
        }

        controller.tickServer();
        receiver.tickServer();

        overrideAspect = getOverrideAspect();

        mergeInterlocks();

        interlock.tick(this);

        SignalAspect prevAspect = controller.getAspect();
        if (receiver.isBeingPaired() || controller.isBeingPaired())
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        else if (controller.isPaired() && receiver.isPaired())
            controller.setAspect(determineAspect());
        else
            controller.setAspect(SignalAspect.BLINK_RED);

        if (prevAspect != controller.getAspect())
            sendUpdateToClient();
    }

    private void mergeInterlocks() {
        for (ForgeDirection side : SIDES) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof TileBoxInterlock) {
                TileBoxInterlock box = (TileBoxInterlock) tile;
                if (box.interlock != interlock) {
                    box.interlock.merge(interlock);
                    return;
                }
            }
        }
    }

    private SignalAspect getOverrideAspect() {
        SignalAspect newAspect = SignalAspect.GREEN;
        for (int side = 2; side < 6; side++) {
            ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
            TileEntity t = tileCache.getTileOnSide(forgeSide);
            if (t instanceof TileBoxBase) {
                TileBoxBase tile = (TileBoxBase) t;
                if (tile.canTransferAspect())
                    newAspect = SignalAspect.mostRestrictive(newAspect, tile.getBoxSignalAspect(forgeSide.getOpposite()));
            }
        }
        return newAspect;
    }

    private SignalAspect determineAspect() {
        interlock.requestLock(this, receiver.getAspect().ordinal() <= SignalAspect.YELLOW.ordinal());
        return interlock.getAspect(this, receiver.getAspect());
    }

    @Override
    public SignalAspect getBoxSignalAspect(ForgeDirection side) {
        return controller.getAspect();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        controller.writeToNBT(data);
        receiver.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        controller.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        controller.readPacketData(data);
        receiver.readPacketData(data);
        markBlockForUpdate();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxInterlock)
            return true;
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canTransferAspect();
        return false;
    }

    @Override
    public boolean canTransferAspect() {
        return false;
    }

    @Override
    public boolean canReceiveAspect() {
        return true;
    }

    @Override
    public SignalController getController() {
        return controller;
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getTriggerAspect() {
        return getBoxSignalAspect(null);
    }

    @Override
    public List<String> getDebugOutput() {
        List<String> debug = super.getDebugOutput();
        debug.add("Interlock Obj: " + interlock);
        debug.add("Interlock Pool: " + interlock.interlocks);
        debug.add("Lock Requests: " + interlock.lockRequests);
        debug.add("Active: " + interlock.active);
        debug.add("Delay: " + interlock.delay);
        debug.add("In Aspect: " + receiver.getAspect().name());
        debug.add("Out Aspect: " + controller.getAspect().name());
        debug.add("Override Aspect: " + overrideAspect.name());
        return debug;
    }

    private static class TileComparator implements Comparator<TileBoxInterlock> {
        public static TileComparator INSTANCE = new TileComparator();

        @Override
        public int compare(TileBoxInterlock o1, TileBoxInterlock o2) {
            if (o1.xCoord != o2.xCoord)
                return o1.xCoord - o2.xCoord;
            if (o1.zCoord != o2.zCoord)
                return o1.zCoord - o2.zCoord;
            if (o1.yCoord != o2.yCoord)
                return o1.yCoord - o2.yCoord;
            return 0;
        }
    }

    private class Interlock {
        private static final int DELAY = 20 * 10;
        private TreeSet<TileBoxInterlock> interlocks = new TreeSet<TileBoxInterlock>(TileComparator.INSTANCE);
        private TreeSet<TileBoxInterlock> lockRequests = new TreeSet<TileBoxInterlock>(TileComparator.INSTANCE);
        private TileBoxInterlock active;
        private int delay;

        public Interlock(TileBoxInterlock tile) {
            interlocks.add(tile);
        }

        public void merge(Interlock interlock) {
            interlocks.addAll(interlock.interlocks);
            for (TileBoxInterlock box : interlocks) {
                box.interlock = this;
            }
        }

        public void tick(TileBoxInterlock host) {
            Iterator<TileBoxInterlock> it = interlocks.iterator();
            while (it.hasNext()) {
                TileBoxInterlock box = it.next();
                if (box.isInvalid()) {
                    it.remove();
                }
            }
            if (delay < DELAY) {
                delay++;
                return;
            }
            if (active != null && active.isInvalid())
                active = null;
            if (active == null && !lockRequests.isEmpty() && interlocks.first() == host) {
                active = lockRequests.last();
                lockRequests.clear();
            }
        }

        public void requestLock(TileBoxInterlock host, boolean request) {
            if (request)
                lockRequests.add(host);
            else if (active == host)
                active = null;
        }

        public SignalAspect getAspect(TileBoxInterlock host, SignalAspect requestedAspect) {
            if (host == active) {
                SignalAspect overrideAspect = SignalAspect.GREEN;
                for (TileBoxInterlock box : interlocks) {
                    overrideAspect = SignalAspect.mostRestrictive(overrideAspect, box.overrideAspect);
                }
                return SignalAspect.mostRestrictive(overrideAspect, requestedAspect);
            }
            return SignalAspect.RED;
        }
    }
}
